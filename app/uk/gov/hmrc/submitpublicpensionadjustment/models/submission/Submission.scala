/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.submitpublicpensionadjustment.models.submission

import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json._
import uk.gov.hmrc.crypto.Sensitive.SensitiveString
import uk.gov.hmrc.crypto.json.JsonEncryption
import uk.gov.hmrc.crypto.{Decrypter, Encrypter}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.CalculationInputs
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.response.CalculationResponse

import java.time.Instant

case class Submission(
  id: String,
  sessionId: String,
  uniqueId: String,
  calculationInputs: CalculationInputs,
  calculation: Option[CalculationResponse],
  lastUpdated: Instant = Instant.now
)

object Submission {

  val reads: Reads[Submission] =
    (
      (__ \ "_id").read[String] and
        (__ \ "sessionId").read[String] and
        (__ \ "uniqueId").read[String] and
        (__ \ "calculationInputs").read[CalculationInputs] and
        (__ \ "calculation").readNullable[CalculationResponse] and
        (__ \ "lastUpdated").read(MongoJavatimeFormats.instantFormat)
    )(Submission.apply _)

  val writes: Writes[Submission] =
    (
      (__ \ "_id").write[String] and
        (__ \ "sessionId").write[String] and
        (__ \ "uniqueId").write[String] and
        (__ \ "calculationInputs").write[CalculationInputs] and
        (__ \ "calculation").writeNullable[CalculationResponse] and
        (__ \ "lastUpdated").write(MongoJavatimeFormats.instantFormat)
    )(unlift(Submission.unapply))

  implicit val format: Format[Submission] = Format(reads, writes)

  def encryptedFormat(implicit crypto: Encrypter with Decrypter): Format[Submission] = {

    import play.api.libs.functional.syntax._

    implicit val sensitiveFormat: Format[SensitiveString] =
      JsonEncryption.sensitiveEncrypterDecrypter(SensitiveString.apply)

    val encryptedReads: Reads[Submission] =
      (
        (__ \ "_id").read[String] and
          (__ \ "sessionId").read[String] and
          (__ \ "uniqueId").read[String] and
          (__ \ "calculationInputs").read[SensitiveString] and
          (__ \ "calculation").readNullable[SensitiveString] and
          (__ \ "lastUpdated").read(MongoJavatimeFormats.instantFormat)
      ) { (id, sessionId, uniqueId, encryptedCalculationInputs, maybeEncryptedCalculation, lastUpdated) =>
        val calculationInputs = Json.parse(encryptedCalculationInputs.decryptedValue).as[CalculationInputs]
        val calculation       = maybeEncryptedCalculation.map(encryptedCalculation =>
          Json.parse(encryptedCalculation.decryptedValue).as[CalculationResponse]
        )
        Submission(id, sessionId, uniqueId, calculationInputs, calculation, lastUpdated)
      }

    val encryptedWrites: Writes[Submission] =
      (
        (__ \ "_id").write[String] and
          (__ \ "sessionId").write[String] and
          (__ \ "uniqueId").write[String] and
          (__ \ "calculationInputs").write[SensitiveString] and
          (__ \ "calculation").writeNullable[SensitiveString] and
          (__ \ "lastUpdated").write(MongoJavatimeFormats.instantFormat)
      ) { s =>
        val maybeEncryptedCalculation: Option[SensitiveString] =
          s.calculation.map(calc => SensitiveString(Json.stringify(Json.toJson(calc))))
        (
          s.id,
          s.sessionId,
          s.uniqueId,
          SensitiveString(Json.stringify(Json.toJson(s.calculationInputs))),
          maybeEncryptedCalculation,
          s.lastUpdated
        )
      }

    Format(encryptedReads orElse reads, encryptedWrites)
  }
}
