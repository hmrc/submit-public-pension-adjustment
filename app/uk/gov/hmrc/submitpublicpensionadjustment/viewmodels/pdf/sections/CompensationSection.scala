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
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.{Formatting, Row, Section}

case class CompensationSection(
  relatingTo: Period,
  directAmount: String,
  indirectAmount: String,
  revisedTaxChargeTotal: String,
  chargeYouPaid: String,
  schemePaidChargeSubSections: Seq[SchemePaidChargeDetailsSubSection] = Seq()
) extends Section
    with Formatting {

  override def orderedFieldNames(): Seq[String] =
    Seq("directAmount", "indirectAmount", "revisedTaxChargeTotal", "chargeYouPaid")

  override def period() = Some(relatingTo)

  override def rows(messages: Messages): Seq[Row] = {

    val subsectionRows = buildSubSectionRows(messages)
    super.rows(messages) ++ subsectionRows
  }

  private def buildSubSectionRows(messages: Messages) =
    schemePaidChargeSubSections.flatMap { ss =>
      Seq(
        Row(
          displayLabel(messages, "schemePaidChargeDetailsSubSection.scheme"),
          ss.index.toString,
          false
        ),
        Row(
          displayLabel(messages, "schemePaidChargeDetailsSubSection.amount"),
          formatPoundsAmount(ss.amount),
          true
        ),
        Row(
          displayLabel(messages, "schemePaidChargeDetailsSubSection.name"),
          ss.name,
          true
        ),
        Row(
          displayLabel(messages, "schemePaidChargeDetailsSubSection.reference"),
          ss.pstr,
          true
        )
      )
    }
}

case class SchemePaidChargeDetailsSubSection(index: Int, amount: Int, name: String, pstr: String) {}

object CompensationSection extends Formatting {

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
      directAmount = formatPoundsAmount(calc.directCompensation),
      indirectAmount = formatPoundsAmount(calc.indirectCompensation),
      revisedTaxChargeTotal = formatPoundsAmount(calc.revisedChargableAmountAfterTaxRate),
      chargeYouPaid = formatPoundsAmount(calc.chargePaidByMember),
      schemePaidChargeSubSections = schemePaidChargeSubSections(finalSubmission, calc)
    )

  private def schemePaidChargeSubSections(
    finalSubmission: FinalSubmission,
    outDateCalc: OutOfDatesTaxYearsCalculation
  ): Seq[SchemePaidChargeDetailsSubSection] = {

    val outDatesSchemes: Seq[TaxYearScheme] = allTaxYears(finalSubmission)
      .filter(ty => ty.period == outDateCalc.period.toCalculationInputsPeriod)
      .flatMap(ty => taxYearSchemes(ty))

    outDatesSchemes.zipWithIndex.map(schemeWithIndex =>
      SchemePaidChargeDetailsSubSection(
        schemeWithIndex._2 + 1,
        schemeWithIndex._1.chargePaidByScheme,
        schemeWithIndex._1.name,
        schemeWithIndex._1.pensionSchemeTaxReference
      )
    )
  }

  private def allTaxYears(finalSubmission: FinalSubmission): Seq[TaxYear2016To2023] =
    finalSubmission.calculationInputs.annualAllowance.map(_.taxYears).getOrElse(List()).collect {
      case ty: TaxYear2016To2023 => ty
    }

  private def taxYearSchemes(taxYear: TaxYear2016To2023): Seq[TaxYearScheme] =
    taxYear match {
      case ty: NormalTaxYear                  => ty.taxYearSchemes
      case ty: InitialFlexiblyAccessedTaxYear => ty.taxYearSchemes
      case ty: PostFlexiblyAccessedTaxYear    => ty.taxYearSchemes
    }
}
