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
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.response.{InDatesTaxYearsCalculation, Period, TaxYearScheme}
import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.{FinalSubmission, SchemeCharge}
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.{Formatting, Row, Section}

case class TaxAdministrationFrameworkSection(
  relatingTo: Period,
  previousChargeAmount: String,
  whoChargePaidBy: String,
  creditValue: String,
  debitValue: String,
  isSchemePayingCharge: String,
  schemePaymentElectionDate: String,
  schemePayingChargeAmount: String,
  schemePayingPstr: String,
  schemePayingName: String,
  schemeDetailsSubSections: Seq[SchemeDetailsSubSection]
) extends Section {

  override def orderedFieldNames(): Seq[String] = Seq(
    "previousChargeAmount",
    "whoChargePaidBy",
    "creditValue",
    "debitValue",
    "isSchemePayingCharge",
    "schemePaymentElectionDate",
    "schemePayingChargeAmount",
    "schemePayingPstr",
    "schemePayingName"
  )

  override def period() = Some(relatingTo)

  override def rows(messages: Messages): Seq[Row] = {
    val rows                     = super.rows(messages)
    val subSectionRows: Seq[Row] = buildSubSectionRows(messages)
    val (firstPart, secondPart)  = rows.splitAt(2)
    firstPart ++ subSectionRows ++ secondPart
  }

  private def buildSubSectionRows(messages: Messages): Seq[Row] =
    schemeDetailsSubSections.flatMap { ss =>
      Seq(
        Row(
          displayLabel(messages, "schemeDetailsSubSection.scheme"),
          ss.index.toString,
          false
        ),
        Row(
          displayLabel(messages, "schemeDetailsSubSection.name"),
          ss.name,
          true
        ),
        Row(
          displayLabel(messages, "schemeDetailsSubSection.reference"),
          ss.pstr,
          true
        )
      )
    }
}

case class SchemeDetailsSubSection(index: Int, name: String, pstr: String) {}

object TaxAdministrationFrameworkSection extends Formatting {
  def build(finalSubmission: FinalSubmission): Seq[TaxAdministrationFrameworkSection] = {
    val inDates = finalSubmission.calculation.map(_.inDates).getOrElse(Seq.empty)

    inDates.map { inDateCalc =>
      val paymentElectionOpt = finalSubmission.submissionInputs.paymentElections
        .find(_.period == inDateCalc.period.toCalculationInputsPeriod)

      val electionSchemeCharge = paymentElectionOpt.flatMap(_.schemeCharge)

      val relevantTaxYear      = findRelevantTaxYear(finalSubmission, inDateCalc)
      val previousChargeAmount = calculatePreviousChargeAmount(relevantTaxYear)

      TaxAdministrationFrameworkSection(
        relatingTo = inDateCalc.period,
        previousChargeAmount = formatPoundsAmount(previousChargeAmount),
        whoChargePaidBy = getWhoChargePaidBy(inDateCalc),
        creditValue = formatPoundsAmount(inDateCalc.memberCredit + inDateCalc.schemeCredit),
        debitValue = formatPoundsAmount(inDateCalc.debit),
        isSchemePayingCharge = format(electionSchemeCharge.map(_.amount).getOrElse(0) > 0),
        schemePaymentElectionDate = getSchemePaymentElectionDate(electionSchemeCharge),
        schemePayingChargeAmount = electionSchemeCharge.map(_.amount.toString).getOrElse(NotApplicable),
        schemePayingPstr = electionSchemeCharge.map(_.schemeDetails.pstr.value).getOrElse(NotApplicable),
        schemePayingName = electionSchemeCharge.map(_.schemeDetails.schemeName).getOrElse(NotApplicable),
        schemeDetailsSubSections = schemeDetailsSubSections(finalSubmission, inDateCalc)
      )
    }
  }

  private def findRelevantTaxYear(
    finalSubmission: FinalSubmission,
    inDateCalc: InDatesTaxYearsCalculation
  ): Option[TaxYear2016To2023] = {
    val relevantTaxYears = finalSubmission.calculationInputs.annualAllowance.map(_.taxYears).getOrElse(List())
    relevantTaxYears
      .collect { case ty: TaxYear2016To2023 => ty }
      .find(_.period == inDateCalc.period.toCalculationInputsPeriod)
  }

  private def calculatePreviousChargeAmount(relevantTaxYear: Option[TaxYear2016To2023]): Int =
    relevantTaxYear match {
      case Some(ty) =>
        ty match {
          case ty: NormalTaxYear                  => ty.chargePaidByMember + ty.taxYearSchemes.map(_.chargePaidByScheme).sum
          case ty: InitialFlexiblyAccessedTaxYear =>
            ty.chargePaidByMember + ty.taxYearSchemes.map(_.chargePaidByScheme).sum
          case ty: PostFlexiblyAccessedTaxYear    =>
            ty.chargePaidByMember + ty.taxYearSchemes.map(_.chargePaidByScheme).sum
        }
      case None     => 0
    }

  private def schemeDetailsSubSections(
    finalSubmission: FinalSubmission,
    inDateCalc: InDatesTaxYearsCalculation
  ): Seq[SchemeDetailsSubSection] = {
    val inDatesSchemes: Seq[TaxYearScheme] = allTaxYears(finalSubmission)
      .filter(ty => ty.period == inDateCalc.period.toCalculationInputsPeriod)
      .flatMap(ty => taxYearSchemes(ty))

    inDatesSchemes.zipWithIndex.map(schemeWithIndex =>
      SchemeDetailsSubSection(
        schemeWithIndex._2 + 1,
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

  private def getWhoChargePaidBy(inDateCalc: InDatesTaxYearsCalculation): String =
    if (inDateCalc.chargePaidByMember > 0 && inDateCalc.chargePaidBySchemes > 0) WhoPaidBoth
    else if (inDateCalc.chargePaidBySchemes > 0) WhoPaidScheme
    else if (inDateCalc.chargePaidByMember > 0) WhoPaidMember
    else WhoPaidNone

  private def getSchemePaymentElectionDate(
    electionSchemeCharge: Option[SchemeCharge]
  ): String =
    electionSchemeCharge
      .flatMap(_.paymentElectionDate)
      .map(date => format(date))
      .orElse(
        electionSchemeCharge
          .flatMap(_.estimatedPaymentElectionQuarter)
      )
      .getOrElse(NotApplicable)
}
