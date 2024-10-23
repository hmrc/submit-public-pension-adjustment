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

package uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.sections

import play.api.i18n.Messages
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.TaxYear2016To2023
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.TaxYear2016To2023.{InitialFlexiblyAccessedTaxYear, NormalTaxYear, PostFlexiblyAccessedTaxYear}
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.response.{OutOfDatesTaxYearsCalculation, Period, TaxYearScheme}
import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.FinalSubmission
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.sections.DeclarationsSection.{format, formatBoolean, formatSchemeCreditConsent}
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.{Formatting, Row, Section}

case class IncomeSubJourneySection(
  revisedPIA: Option[Int],
  reducedNetIncome: Option[Int],
  thresholdIncomeAmount: Option[Int],
  adjustedIncome: Option[Int],
  personalAllowance: Option[Int],
) extends Section
    with Formatting {

  override def orderedFieldNames(): Seq[String] =
    Seq("revisedPIA", "reducedNetIncome", "thresholdIncomeAmount", "adjustedIncome", "personalAllowance")

}

//case class SomethingSubSection(index: Int, amount: Int, name: String, pstr: String) {}

object IncomeSubJourneySection extends Formatting {

  def build(finalSubmission: FinalSubmission): IncomeSubJourneySection =
    IncomeSubJourneySection(
      revisedPIA = Some(100),
      reducedNetIncome = Some(50),
      thresholdIncomeAmount = Some(500),
      adjustedIncome = Some(450),
      personalAllowance = Some(600)


    )

//  def build(finalSubmission: FinalSubmission): Seq[IncomeSubJourneySection] = {
//    val outOfDates = finalSubmission.calculation
//      .map(_.outDates)
//      .getOrElse(Seq.empty)
//
//    outOfDates.map(calc => buildFromOutOfDates(calc, finalSubmission))
//  }
//
//  private def buildFromOutOfDates(
//    calc: OutOfDatesTaxYearsCalculation,
//    finalSubmission: FinalSubmission
//  ): IncomeSubJourneySection =
//    IncomeSubJourneySection(
//      thresholdIncomeAmount = finalSubmission.submissionInputs.incomeSubJourneyValues.map(_.thresholdIncomeAmount)
//    )
//
//  private def schemePaidChargeSubSections(
//    finalSubmission: FinalSubmission,
//    outDateCalc: OutOfDatesTaxYearsCalculation
//  ): Seq[SchemePaidChargeDetailsSubSection] = {
//
//    val outDatesSchemes: Seq[TaxYearScheme] = allTaxYears(finalSubmission)
//      .filter(ty => ty.period == outDateCalc.period.toCalculationInputsPeriod)
//      .flatMap(ty => taxYearSchemes(ty))
//
//    outDatesSchemes.zipWithIndex.map(schemeWithIndex =>
//      SchemePaidChargeDetailsSubSection(
//        schemeWithIndex._2 + 1,
//        schemeWithIndex._1.chargePaidByScheme,
//        schemeWithIndex._1.name,
//        schemeWithIndex._1.pensionSchemeTaxReference
//      )
//    )
//  }
//
//  private def allTaxYears(finalSubmission: FinalSubmission): Seq[TaxYear2016To2023] =
//    finalSubmission.calculationInputs.annualAllowance.map(_.taxYears).getOrElse(List()).collect {
//      case ty: TaxYear2016To2023 => ty
//    }
//
//  private def taxYearSchemes(taxYear: TaxYear2016To2023): Seq[TaxYearScheme] =
//    taxYear match {
//      case ty: NormalTaxYear                  => ty.taxYearSchemes
//      case ty: InitialFlexiblyAccessedTaxYear => ty.taxYearSchemes
//      case ty: PostFlexiblyAccessedTaxYear    => ty.taxYearSchemes
//    }
}
