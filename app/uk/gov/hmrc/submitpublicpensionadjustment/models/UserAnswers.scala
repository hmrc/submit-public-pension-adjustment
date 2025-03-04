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

import play.api.libs.json.*
import uk.gov.hmrc.crypto.Sensitive.*
import uk.gov.hmrc.crypto.json.JsonEncryption
import uk.gov.hmrc.crypto.{Decrypter, Encrypter}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant

final case class UserAnswers(
  id: String,
  data: JsObject = Json.obj(),
  lastUpdated: Instant = Instant.now
)

object UserAnswers {

  val reads: Reads[UserAnswers] = {

    import play.api.libs.functional.syntax.*

    (
      (__ \ "_id").read[String] and
        (__ \ "data").read[JsObject] and
        (__ \ "lastUpdated").read(MongoJavatimeFormats.instantFormat)
    )(UserAnswers.apply _)
  }

  val writes: OWrites[UserAnswers] = {

    import play.api.libs.functional.syntax.*

    (
      (__ \ "_id").write[String] and
        (__ \ "data").write[JsObject] and
        (__ \ "lastUpdated").write(MongoJavatimeFormats.instantFormat)
    )(o => Tuple.fromProductTyped(o))
  }

  implicit val format: OFormat[UserAnswers] = OFormat(reads, writes)

  def encryptedFormat(implicit crypto: Encrypter with Decrypter): OFormat[UserAnswers] = {

    import play.api.libs.functional.syntax.*

    implicit val sensitiveFormat: Format[SensitiveString] =
      JsonEncryption.sensitiveEncrypterDecrypter(SensitiveString.apply)

    val encryptedReads: Reads[UserAnswers] =
      (
        (__ \ "_id").read[String] and
          (__ \ "data").read[SensitiveString] and
          (__ \ "lastUpdated").read(MongoJavatimeFormats.instantFormat)
      )((id, data, lastUpdated) => UserAnswers(id, Json.parse(data.decryptedValue).as[JsObject], lastUpdated))

    val encryptedWrites: OWrites[UserAnswers] =
      (
        (__ \ "_id").write[String] and
          (__ \ "data").write[SensitiveString] and
          (__ \ "lastUpdated").write(MongoJavatimeFormats.instantFormat)
      )(ua => (ua.id, SensitiveString(Json.stringify(ua.data)), ua.lastUpdated))

    OFormat(encryptedReads orElse reads, encryptedWrites)
  }
}
