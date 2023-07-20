/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.submitpublicpensionadjustment.models.bars

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.submitpublicpensionadjustment.models.{Dan, EmpRef, TaxId, Vrn, Zppt, Zsdl}

final case class TaxIdKey(value: String)

object TaxIdKey {
  implicit val format: Format[TaxIdKey] = Json.valueFormat

  def apply(taxId: TaxId): TaxIdKey = taxId match {
    case Zsdl(value)   => TaxIdKey(s"Zsdl-$value")
    case Vrn(value)    => TaxIdKey(s"Vrn-$value")
    case Dan(value)    => TaxIdKey(s"Dan-$value")
    case Zppt(value)   => TaxIdKey(s"Zppt-$value")
    case EmpRef(value) => TaxIdKey(s"EmpRef-$value")
  }
}
