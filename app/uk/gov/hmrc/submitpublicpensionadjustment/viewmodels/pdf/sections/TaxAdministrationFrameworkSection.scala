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

import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.response.Period
import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.FinalSubmission
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.Section

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
      // val paymentElectionOpt = finalSubmission.submissionInputs.paymentElections.find(_.period == inDateCalc.period) // todo will change
      // val electionSchemeCharge = paymentElectionOpt.flatMap(_.schemeCharge).getOrElse(throw new RuntimeException("No corresponding SchemeCharge found"))
      TaxAdministrationFrameworkSection(
        relatingTo = inDateCalc.period,
        previousChargeAmount = "todo", // inputs or response
        whoChargePaidBy =
          "todo", // if (inDateCalc.chargePaidByMember > 0) "Member" else "Scheme", // in payment election
        previousChargePaidBySchemeName =
          "todo", // electionSchemeCharge.schemeDetails.schemeName, // calculation response
        previousChargePaidByPstr = "todo", // electionSchemeCharge.schemeDetails.pstr.value, // inputs or response
        creditValue = inDateCalc.memberCredit.toString,
        debitValue = inDateCalc.debit.toString,
        isSchemePayingCharge = "todo", // if (inDateCalc.chargePaidBySchemes > 0) "Yes" else "No",
        schemePaymentElectionDate =
          "todo", // electionSchemeCharge.paymentElectionDate.map(_.toString).getOrElse(""), //in submission inputs
        schemePayingChargeAmount = "todo", // submission inputs how much the scheme pay
        schemePayingPstr = "todo", // electionSchemeCharge.schemeDetails.pstr.value,
        schemePayingName = "todo" // electionSchemeCharge.schemeDetails.schemeName // na
      )
    }
  }
}
