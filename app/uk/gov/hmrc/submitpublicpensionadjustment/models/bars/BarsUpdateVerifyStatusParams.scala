package uk.gov.hmrc.submitpublicpensionadjustment.models.bars

import play.api.libs.json.{Format, Json}

final case class BarsUpdateVerifyStatusParams(taxId: TaxIdKey)

object BarsUpdateVerifyStatusParams {
  implicit val format: Format[BarsUpdateVerifyStatusParams] = Json.format
}