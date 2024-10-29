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
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.{IncomeSubJourney, TaxYear2016To2023}
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.TaxYear2016To2023.{InitialFlexiblyAccessedTaxYear, NormalTaxYear, PostFlexiblyAccessedTaxYear}
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.response.TaxYearScheme
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.sections.CompensationSection.{allTaxYears, taxYearSchemes}
//import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.response.{OutOfDatesTaxYearsCalculation, Period, TaxYearScheme}
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.Period
import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.FinalSubmission
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.sections.DeclarationsSection.{format, formatBoolean, formatSchemeCreditConsent}
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.{Formatting, Row, Section}

case class IncomeSubJourneySection(
  periodRange: String,
  incomeSubJourneySubSection: IncomeSubJourneySubSection
  //                                   reducedNetIncome: String,
//                                   thresholdIncomeAmount: String,
//                                   adjustedIncomeAmount: String,
//                                   personalAllowanceAmount: String
) extends Section
    with Formatting {

  override def orderedFieldNames(): Seq[String] =
    Seq("periodRange")

  override def rows(messages: Messages): Seq[Row] = {

    val subsectionRows = buildSubSectionRows(messages)
    super.rows(messages) ++ subsectionRows
  }

  private def buildSubSectionRows(messages: Messages): Seq[Row] =
    Seq(
      Row(
        displayLabel(messages, "incomeSubJourneySubSection.reducedNetIncome"),
        incomeSubJourneySubSection.reducedNetIncome,
        true
      ),
      Row(
        displayLabel(messages, "incomeSubJourneySubSection.thresholdIncomeAmount"),
        incomeSubJourneySubSection.thresholdIncomeAmount,
        true
      ),
      Row(
        displayLabel(messages, "incomeSubJourneySubSection.adjustedIncomeAmount"),
        incomeSubJourneySubSection.adjustedIncomeAmount,
        true
      ),
      Row(
        displayLabel(messages, "incomeSubJourneySubSection.personalAllowanceAmount"),
        incomeSubJourneySubSection.personalAllowanceAmount,
        true
      )
    )

}

case class IncomeSubJourneySubSection(
  reducedNetIncome: String,
  thresholdIncomeAmount: String,
  adjustedIncomeAmount: String,
  personalAllowanceAmount: String
) {}

object IncomeSubJourneySection extends Formatting {

//  def build(finalSubmission: FinalSubmission): IncomeSubJourneySection              =
//    IncomeSubJourneySection(
//      incomeSubJourneyTitle = "Income sub journey",
//      incomeSubJourneySubSection = incomeSubJourneySubSection(finalSubmission)
//    )

  def build(finalSubmission: FinalSubmission): Seq[IncomeSubJourneySection] = {
    val taxYears = allTaxYears(finalSubmission)
    taxYears.map { taxYear =>
      IncomeSubJourneySection(
        formatPeriodToRange(taxYear.period),
        incomeSubJourneySubSection(taxYear)
      )
    }
  }

  private def incomeSubJourneySubSection(taxYear: TaxYear2016To2023): IncomeSubJourneySubSection =
    IncomeSubJourneySubSection(
      formatOptPoundsAmount(taxYearIncomeSubJourney(taxYear).reducedNetIncomeAmount),
      formatOptPoundsAmount(taxYearIncomeSubJourney(taxYear).thresholdIncomeAmount),
      formatOptPoundsAmount(taxYearIncomeSubJourney(taxYear).adjustedIncomeAmount),
      formatOptPoundsAmount(taxYearIncomeSubJourney(taxYear).personalAllowanceAmount)
    )

//  private def incomeSubJourneySubSection(
//    finalSubmission: FinalSubmission
//  ): Seq[IncomeSubJourneySubSection] = {
//    val taxYears = allTaxYears(finalSubmission)
//
//    taxYears.map(taxYear =>
//      IncomeSubJourneySubSection(
//        formatOptPoundsAmount(taxYearIncomeSubJourney(taxYear).reducedNetIncomeAmount),
//        formatOptPoundsAmount(taxYearIncomeSubJourney(taxYear).thresholdIncomeAmount),
//        formatOptPoundsAmount(taxYearIncomeSubJourney(taxYear).adjustedIncomeAmount),
//        formatOptPoundsAmount(taxYearIncomeSubJourney(taxYear).personalAllowanceAmount)
//      )
//    )
//  }
  private def allTaxYears(finalSubmission: FinalSubmission): Seq[TaxYear2016To2023] =
    finalSubmission.calculationInputs.annualAllowance.map(_.taxYears).getOrElse(List()).collect {
      case ty: TaxYear2016To2023 => ty
    }

  private def taxYearIncomeSubJourney(taxYear: TaxYear2016To2023): IncomeSubJourney =
    taxYear match {
      case ty: NormalTaxYear                  => ty.incomeSubJourney
      case ty: InitialFlexiblyAccessedTaxYear => ty.incomeSubJourney
      case ty: PostFlexiblyAccessedTaxYear    => ty.incomeSubJourney
    }

}
