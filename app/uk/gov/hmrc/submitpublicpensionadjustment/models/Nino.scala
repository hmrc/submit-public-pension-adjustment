/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.submitpublicpensionadjustment.models

import play.api.libs.json.{Format, Json}
import play.api.mvc.PathBindable

case class Nino(value: String) extends AnyVal

object Nino {
  implicit val format: Format[Nino] = Json.valueFormat
  implicit val pathBinder: PathBindable[Nino] = PathBindable.anyValPathBindable[Nino]
}