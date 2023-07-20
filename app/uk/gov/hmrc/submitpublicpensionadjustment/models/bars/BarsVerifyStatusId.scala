/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.submitpublicpensionadjustment.models.bars

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.submitpublicpensionadjustment.models.Nino

final case class BarsVerifyStatusId(value: String)

object BarsVerifyStatusId {
  implicit val format: Format[BarsVerifyStatusId] = Json.valueFormat

  def from(nino: Nino): BarsVerifyStatusId = BarsVerifyStatusId(nino.value)
}