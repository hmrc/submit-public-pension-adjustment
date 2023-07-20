/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.submitpublicpensionadjustment.models.bars

import play.api.libs.json.{Format, Json, OFormat}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant

final case class BarsVerifyStatus(
                                   _id:                   BarsVerifyStatusId,
                                   verifyCalls:           NumberOfBarsVerifyAttempts,
                                   createdAt:             Instant                    = Instant.now,
                                   lastUpdated:           Instant                    = Instant.now,
                                   lockoutExpiryDateTime: Option[Instant]            = None
                                 )

object BarsVerifyStatus {

  implicit val format: OFormat[BarsVerifyStatus] = Json.format

  def apply(id: BarsVerifyStatusId): BarsVerifyStatus = BarsVerifyStatus(
    _id         = id,
    verifyCalls = NumberOfBarsVerifyAttempts(1)
  )
}

final case class EncryptedBarsVerifyStatus(
                                            _id:                   String, // encrypted BarsVerifyStatusId
                                            verifyCalls:           NumberOfBarsVerifyAttempts,
                                            createdAt:             Instant                    = Instant.now,
                                            lastUpdated:           Instant                    = Instant.now,
                                            lockoutExpiryDateTime: Option[Instant]            = None
                                          )

object EncryptedBarsVerifyStatus {
  implicit val instantFormat: Format[Instant] = MongoJavatimeFormats.instantFormat
  implicit val format: OFormat[EncryptedBarsVerifyStatus] = Json.format
}
