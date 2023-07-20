/*
 * Copyright 2023 HM Revenue & Customs
 *
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
