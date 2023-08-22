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

package uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.sections

import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.TaxYear2016To2023
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.TaxYear2016To2023.{InitialFlexiblyAccessedTaxYear, NormalTaxYear, PostFlexiblyAccessedTaxYear}
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.response.{OutOfDatesTaxYearsCalculation, Period}
import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.FinalSubmission
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.Section

case class CompensationSection(
  relatingTo: Period,
  directAmount: String,
  indirectAmount: String,
  revisedTaxChargeTotal: String,
  chargeYouPaid: String,
  additionalRows: Seq[(String, String)] = Seq()
) extends Section {

  override def orderedFieldNames(): Seq[String] =
    Seq("directAmount", "indirectAmount", "revisedTaxChargeTotal", "chargeYouPaid")

  override def period() = Some(relatingTo)
}

object CompensationSection {

  def build(finalSubmission: FinalSubmission): Seq[CompensationSection] = {
    val outOfDates = finalSubmission.calculation
      .map(_.outDates)
      .getOrElse(Seq.empty)

    outOfDates.map(calc => buildFromOutOfDates(calc, finalSubmission))
  }

  private def buildFromOutOfDates(
    calc: OutOfDatesTaxYearsCalculation,
    finalSubmission: FinalSubmission
  ): CompensationSection =
    CompensationSection(
      relatingTo = calc.period,
      directAmount = s"£${calc.directCompensation.toString}",
      indirectAmount = s"£${calc.indirectCompensation.toString}",
      revisedTaxChargeTotal = s"£${calc.revisedChargableAmountAfterTaxRate.toString}",
      chargeYouPaid = s"£${calc.chargePaidByMember.toString}",
      additionalRows = getAdditionalRows(finalSubmission, calc).flatten
    )

  private def getAdditionalRows(
    finalSubmission: FinalSubmission,
    outDateCalc: OutOfDatesTaxYearsCalculation
  ): Seq[Seq[(String, String)]] = {
    val additionalRows = for {
      taxYear <- finalSubmission.calculationInputs.annualAllowance.map(_.taxYears).getOrElse(List()).collect {
                   case ty: TaxYear2016To2023 => ty
                 }
      if taxYear.period == outDateCalc.period.toCalculationInputsPeriod
      scheme  <- taxYear match {
                   case ny: NormalTaxYear                     => ny.taxYearSchemes
                   case ifaty: InitialFlexiblyAccessedTaxYear => ifaty.taxYearSchemes
                   case pfaty: PostFlexiblyAccessedTaxYear    => pfaty.taxYearSchemes
                 }
    } yield Seq(
      ("scheme", ""),
      ("chargeSchemePaid", s"£${scheme.chargePaidByScheme}"),
      ("originalSchemePaidChargeName", scheme.name),
      ("originalSchemePaidChargePstr", scheme.pensionSchemeTaxReference)
    )

    indexAdditionalRows(additionalRows)
  }

  private def indexAdditionalRows(additionalRows: Seq[Seq[(String, String)]]): Seq[Seq[(String, String)]] =
    additionalRows.zipWithIndex.map { case (row, index) =>
      row.map {
        case ("scheme", "") => ("scheme", (index + 1).toString)
        case other          => other
      }
    }

}
