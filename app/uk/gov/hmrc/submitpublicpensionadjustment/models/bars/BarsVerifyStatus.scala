/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.submitpublicpensionadjustment.models.bars

import play.api.libs.json.{Format, Json, OFormat}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant

final case class BarsVerifyStatus(
    _id:                   TaxIdKey,
    verifyCalls:           NumberOfBarsVerifyAttempts,
    createdAt:             Instant                    = Instant.now,
    lastUpdated:           Instant                    = Instant.now,
    lockoutExpiryDateTime: Option[Instant]            = None
)

object BarsVerifyStatus {

  implicit val instantFormat: Format[Instant] = MongoJavatimeFormats.instantFormat
  implicit val format: OFormat[BarsVerifyStatus] = Json.format

  def apply(taxId: TaxIdKey): BarsVerifyStatus = BarsVerifyStatus(
    _id         = taxId,
    verifyCalls = NumberOfBarsVerifyAttempts(1)
  )
}

