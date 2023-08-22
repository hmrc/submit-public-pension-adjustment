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
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.response.{InDatesTaxYearsCalculation, Period}
import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.{FinalSubmission, PersonalCharge, SchemeCharge}
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.Section

import java.time.format.DateTimeFormatter

case class TaxAdministrationFrameworkSection(
                                              relatingTo: Period,
                                              previousChargeAmount: String,
                                              whoChargePaidBy: String,
                                              additionalRows: Seq[(String, String)] = Seq(),
                                              creditValue: String,
                                              debitValue: String,
                                              isSchemePayingCharge: String,
                                              schemePaymentElectionDate: String,
                                              schemePayingChargeAmount: String,
                                              schemePayingPstr: String,
                                              schemePayingName: String
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
}

object TaxAdministrationFrameworkSection {
  def build(finalSubmission: FinalSubmission): Seq[TaxAdministrationFrameworkSection] = {
    val inDates = finalSubmission.calculation.map(_.inDates).getOrElse(Seq.empty)

    inDates.map { inDateCalc =>
      val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

      val paymentElectionOpt = finalSubmission.submissionInputs.paymentElections
        .find(_.period == inDateCalc.period.toCalculationInputsPeriod)

      val electionSchemeCharge = paymentElectionOpt.flatMap(_.schemeCharge)
      val electionPersonalCharge = paymentElectionOpt.flatMap(_.personalCharge)

      val relevantTaxYear = findRelevantTaxYear(finalSubmission, inDateCalc)
      val previousChargeAmount = calculatePreviousChargeAmount(relevantTaxYear)


      TaxAdministrationFrameworkSection(
        relatingTo = inDateCalc.period,
        previousChargeAmount = s"£${previousChargeAmount.toString}",
        whoChargePaidBy = getWhoChargePaidBy(electionSchemeCharge, electionPersonalCharge),
        creditValue = s"£${inDateCalc.memberCredit}",
        debitValue = s"£${inDateCalc.debit}",
        isSchemePayingCharge = if (electionSchemeCharge.isDefined) "Yes" else "No",
        schemePaymentElectionDate = getSchemePaymentElectionDate(electionSchemeCharge, dateFormatter),
        schemePayingChargeAmount = electionSchemeCharge.map(_.amount.toString).getOrElse("Not applicable"),
        schemePayingPstr = electionSchemeCharge.map(_.schemeDetails.pstr.value).getOrElse("Not applicable"),
        schemePayingName = electionSchemeCharge.map(_.schemeDetails.schemeName).getOrElse("Not applicable"),
        additionalRows = getAdditionalRows(finalSubmission, inDateCalc).flatten
      )
    }
  }

  private def findRelevantTaxYear(finalSubmission: FinalSubmission, inDateCalc: InDatesTaxYearsCalculation): Option[TaxYear2016To2023] = {
    val relevantTaxYears = finalSubmission.calculationInputs.annualAllowance.map(_.taxYears).getOrElse(List())
    relevantTaxYears.collect { case ty: TaxYear2016To2023 => ty }
      .find(_.period == inDateCalc.period.toCalculationInputsPeriod)
  }

  private def calculatePreviousChargeAmount(relevantTaxYear: Option[TaxYear2016To2023]): Int = {
    relevantTaxYear match {
      case Some(ty) => ty match {
        case ny: NormalTaxYear => ny.chargePaidByMember + ny.taxYearSchemes.map(_.chargePaidByScheme).sum
        case ifaty: InitialFlexiblyAccessedTaxYear => ifaty.chargePaidByMember + ifaty.taxYearSchemes.map(_.chargePaidByScheme).sum
        case pfaty: PostFlexiblyAccessedTaxYear => pfaty.chargePaidByMember + pfaty.taxYearSchemes.map(_.chargePaidByScheme).sum
      }
      case None => 0
    }
  }

  private def getAdditionalRows(finalSubmission: FinalSubmission, inDateCalc: InDatesTaxYearsCalculation): Seq[Seq[(String, String)]] = {
    val additionalRows = for {
      taxYear <- finalSubmission.calculationInputs.annualAllowance.map(_.taxYears).getOrElse(List()).collect {
        case ty: TaxYear2016To2023 => ty
      }
      if taxYear.period == inDateCalc.period.toCalculationInputsPeriod
      scheme <- taxYear match {
        case ny: NormalTaxYear => ny.taxYearSchemes
        case ifaty: InitialFlexiblyAccessedTaxYear => ifaty.taxYearSchemes
        case pfaty: PostFlexiblyAccessedTaxYear => pfaty.taxYearSchemes
      }
    } yield Seq(
      ("scheme", ""),
      ("previousChargePaidBySchemeName", scheme.name),
      ("previousChargePaidByPstr", scheme.pensionSchemeTaxReference)
    )

    indexAdditionalRows(additionalRows)
  }

  private def indexAdditionalRows(additionalRows: Seq[Seq[(String, String)]]): Seq[Seq[(String, String)]] = {
    additionalRows.zipWithIndex.map { case (row, index) =>
      row.map {
        case ("scheme", "") => ("scheme", (index + 1).toString)
        case other => other
      }
    }
  }

  private def getWhoChargePaidBy(electionSchemeCharge: Option[SchemeCharge], electionPersonalCharge: Option[PersonalCharge]): String = {
    if (electionSchemeCharge.isDefined && electionPersonalCharge.isDefined) "Both"
    else if (electionSchemeCharge.isDefined) "Scheme"
    else if (electionPersonalCharge.isDefined) "Member"
    else "None"
  }

  private def getSchemePaymentElectionDate(electionSchemeCharge: Option[SchemeCharge], dateFormatter: DateTimeFormatter): String = {
    electionSchemeCharge
      .flatMap(_.paymentElectionDate)
      .map(date => dateFormatter.format(date))
      .orElse(
        electionSchemeCharge
          .flatMap(_.estimatedPaymentElectionQuarter)
      )
      .getOrElse("Not applicable")
  }
}
