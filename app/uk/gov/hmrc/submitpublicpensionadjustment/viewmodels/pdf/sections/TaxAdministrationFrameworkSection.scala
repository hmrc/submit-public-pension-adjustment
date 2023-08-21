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

import cats.implicits.toFunctorOps
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.response.Period
import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.{FinalSubmission, PersonalCharge, SchemeCharge}
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.Section

import java.time.format.DateTimeFormatter

case class TaxAdministrationFrameworkSection(
  relatingTo: Period,
  previousChargeAmount: String,
  whoChargePaidBy: String,
  previousChargePaidBySchemeName: String,
  previousChargePaidByPstr: String,
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
    "previousChargePaidBySchemeName",
    "previousChargePaidByPstr",
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
    val inDates = finalSubmission.calculation
      .map(_.inDates)
      .getOrElse(Seq.empty)

    inDates.map { inDateCalc =>
      val paymentElectionOpt                             = finalSubmission.submissionInputs.paymentElections.find(
        _.period == inDateCalc.period.toCalculationInputsPeriod
      )
      val electionSchemeCharge: Option[SchemeCharge]     = paymentElectionOpt.flatMap(_.schemeCharge)
      val electionPersonalCharge: Option[PersonalCharge] = paymentElectionOpt.flatMap(_.personalCharge)
      val dateFormatter                                  = DateTimeFormatter.ofPattern("dd/MM/yyyy")
      TaxAdministrationFrameworkSection(
        relatingTo = inDateCalc.period,
        previousChargeAmount =
          inDateCalc.chargePaidBySchemes.toString, // There is a List[TaxYearScheme] in inputs, which one do I map..
        whoChargePaidBy = if (electionSchemeCharge.isDefined && electionPersonalCharge.isDefined) { "Both" }
        else if (electionSchemeCharge.isDefined) { "Scheme" }
        else if (electionPersonalCharge.isDefined) { "Member" }
        else "None",
        previousChargePaidBySchemeName = electionSchemeCharge
          .map(_.schemeDetails)
          .map(_.schemeName)
          .getOrElse("Not applicable"),
        previousChargePaidByPstr = electionSchemeCharge
          .map(_.schemeDetails)
          .map(_.pstr.value)
          .getOrElse("Not applicable"),
        creditValue = "£" + inDateCalc.memberCredit.toString,
        debitValue = "£" + inDateCalc.debit.toString,
        isSchemePayingCharge = if (electionSchemeCharge.isDefined) "Yes" else "No",
        schemePaymentElectionDate = electionSchemeCharge
          .flatMap(_.paymentElectionDate)
          .map(date => dateFormatter.format(date))
          .getOrElse(
            "Not Entered"
          ),
        schemePayingChargeAmount = electionSchemeCharge
          .map(_.amount.toString)
          .getOrElse("Not applicable"),
        schemePayingPstr = electionSchemeCharge
          .map(_.schemeDetails)
          .map(_.pstr.value)
          .getOrElse("Not applicable"),
        schemePayingName = electionSchemeCharge
          .map(_.schemeDetails)
          .map(_.schemeName)
          .getOrElse("Not applicable")
      )
    }
  }
}
