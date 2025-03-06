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

package uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.response

import play.api.libs.json.{Reads, Writes, __}

case class InDatesTaxYearsCalculation(
  period: Period,
  memberCredit: Int,
  schemeCredit: Int,
  debit: Int,
  chargePaidByMember: Int,
  chargePaidBySchemes: Int,
  revisedChargableAmountBeforeTaxRate: Int,
  revisedChargableAmountAfterTaxRate: Int,
  unusedAnnualAllowance: Int,
  taxYearSchemes: List[InDatesTaxYearSchemeCalculation],
  totalCompensation: Option[Int]
)

object InDatesTaxYearsCalculation {

  implicit lazy val reads: Reads[InDatesTaxYearsCalculation] = {

    import play.api.libs.functional.syntax.*

    (
      (__ \ "period").read[Period] and
        (__ \ "memberCredit").read[Int] and
        (__ \ "schemeCredit").read[Int] and
        (__ \ "debit").read[Int] and
        (__ \ "chargePaidByMember").read[Int] and
        (__ \ "chargePaidBySchemes").read[Int] and
        (__ \ "revisedChargableAmountBeforeTaxRate").read[Int] and
        (__ \ "revisedChargableAmountAfterTaxRate").read[Int] and
        (__ \ "unusedAnnualAllowance").read[Int] and
        (__ \ "taxYearSchemes").read[List[InDatesTaxYearSchemeCalculation]] and
        (__ \ "totalCompensation").readNullable[Int]
    )(InDatesTaxYearsCalculation.apply _)
  }

  implicit lazy val writes: Writes[InDatesTaxYearsCalculation] = {

    import play.api.libs.functional.syntax.*

    (
      (__ \ "period").write[Period] and
        (__ \ "memberCredit").write[Int] and
        (__ \ "schemeCredit").write[Int] and
        (__ \ "debit").write[Int] and
        (__ \ "chargePaidByMember").write[Int] and
        (__ \ "chargePaidBySchemes").write[Int] and
        (__ \ "revisedChargableAmountBeforeTaxRate").write[Int] and
        (__ \ "revisedChargableAmountAfterTaxRate").write[Int] and
        (__ \ "unusedAnnualAllowance").write[Int] and
        (__ \ "taxYearSchemes").write[List[InDatesTaxYearSchemeCalculation]] and
        (__ \ "totalCompensation").writeNullable[Int]
    )(a =>
      (
        a.period,
        a.memberCredit,
        a.schemeCredit,
        a.debit,
        a.chargePaidByMember,
        a.chargePaidBySchemes,
        a.revisedChargableAmountBeforeTaxRate,
        a.revisedChargableAmountAfterTaxRate,
        a.unusedAnnualAllowance,
        a.taxYearSchemes,
        a.totalCompensation
      )
    )
  }

}
