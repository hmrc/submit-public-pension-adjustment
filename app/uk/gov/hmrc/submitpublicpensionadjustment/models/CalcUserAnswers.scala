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

package uk.gov.hmrc.submitpublicpensionadjustment.models

import play.api.libs.json._
import uk.gov.hmrc.crypto.Sensitive._
import uk.gov.hmrc.crypto.json.JsonEncryption
import uk.gov.hmrc.crypto.{Decrypter, Encrypter}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant

final case class CalcUserAnswers(
  id: String,
  data: JsObject,
  uniqueId: String,
  lastUpdated: Instant,
  authenticated: Boolean = false,
  submissionStarted: Boolean = false
)

object CalcUserAnswers {

  val reads: Reads[CalcUserAnswers] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "_id").read[String] and
        (__ \ "data").read[JsObject] and
        (__ \ "uniqueId").read[String] and
        (__ \ "lastUpdated").read(MongoJavatimeFormats.instantFormat) and
        (__ \ "authenticated").read[Boolean] and
        (__ \ "submissionStarted").read[Boolean]
    )(CalcUserAnswers.apply _)
  }

  val writes: OWrites[CalcUserAnswers] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "_id").write[String] and
        (__ \ "data").write[JsObject] and
        (__ \ "uniqueId").write[String] and
        (__ \ "lastUpdated").write(MongoJavatimeFormats.instantFormat) and
        (__ \ "authenticated").write[Boolean] and
        (__ \ "submissionStarted").write[Boolean]
    )(unlift(CalcUserAnswers.unapply))
  }

  implicit val format: OFormat[CalcUserAnswers] = OFormat(reads, writes)

  def encryptedFormat(implicit crypto: Encrypter with Decrypter): OFormat[CalcUserAnswers] = {

    import play.api.libs.functional.syntax._

    implicit val sensitiveFormat: Format[SensitiveString] =
      JsonEncryption.sensitiveEncrypterDecrypter(SensitiveString.apply)

    val encryptedReads: Reads[CalcUserAnswers] =
      (
        (__ \ "_id").read[String] and
          (__ \ "data").read[SensitiveString] and
          (__ \ "uniqueId").read[String] and
          (__ \ "lastUpdated").read(MongoJavatimeFormats.instantFormat) and
          (__ \ "authenticated").read[Boolean] and
          (__ \ "submissionStarted").read[Boolean]
      )((id, data, uniqueId, lastUpdated, authenticated, submissionStarted) =>
        CalcUserAnswers(
          id,
          Json.parse(data.decryptedValue).as[JsObject],
          uniqueId,
          lastUpdated,
          authenticated,
          submissionStarted
        )
      )

    val encryptedWrites: OWrites[CalcUserAnswers] =
      (
        (__ \ "_id").write[String] and
          (__ \ "data").write[SensitiveString] and
          (__ \ "uniqueId").write[String] and
          (__ \ "lastUpdated").write(MongoJavatimeFormats.instantFormat) and
          (__ \ "authenticated").write[Boolean] and
          (__ \ "submissionStarted").write[Boolean]
      )(ua =>
        (
          ua.id,
          SensitiveString(Json.stringify(ua.data)),
          ua.uniqueId,
          ua.lastUpdated,
          ua.authenticated,
          ua.submissionStarted
        )
      )

    OFormat(encryptedReads orElse reads, encryptedWrites)
  }
}
