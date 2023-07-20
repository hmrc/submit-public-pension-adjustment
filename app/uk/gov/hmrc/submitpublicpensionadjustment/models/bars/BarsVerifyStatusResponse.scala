/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.submitpublicpensionadjustment.models.bars

import play.api.libs.json.{Json, OFormat}

import java.time.Instant

final case class BarsVerifyStatusResponse(attempts: NumberOfBarsVerifyAttempts, lockoutExpiryDateTime: Option[Instant])

object BarsVerifyStatusResponse {
  implicit val format: OFormat[BarsVerifyStatusResponse] = Json.format

  def apply(status: BarsVerifyStatus): BarsVerifyStatusResponse =
    BarsVerifyStatusResponse(status.verifyCalls, status.lockoutExpiryDateTime)
}
