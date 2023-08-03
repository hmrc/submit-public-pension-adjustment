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

  // TODO - Need to map values from final submission.
  def build(finalSubmission: FinalSubmission): Seq[TaxAdministrationFrameworkSection] = Seq(
    TaxAdministrationFrameworkSection(
      relatingTo = Period._2018,
      previousChargeAmount = "previousChargeAmount1",
      whoChargePaidBy = "whoChargePaidBy",
      previousChargePaidBySchemeName = "previousChargePaidBySchemeName1",
      previousChargePaidByPstr = "previousChargePaidByPstr",
      creditValue = "creditValue",
      debitValue = "debitValue",
      isSchemePayingCharge = "isSchemePayingCharge",
      schemePaymentElectionDate = "schemePaymentElectionDate",
      schemePayingChargeAmount = "schemePayingChargeAmount",
      schemePayingPstr = "schemePayingPstr1",
      schemePayingName = "schemePayingName1"
    ),
    TaxAdministrationFrameworkSection(
      relatingTo = Period._2019,
      previousChargeAmount = "previousChargeAmount2",
      whoChargePaidBy = "whoChargePaidBy",
      previousChargePaidBySchemeName = "previousChargePaidBySchemeName2",
      previousChargePaidByPstr = "previousChargePaidByPstr",
      creditValue = "creditValue",
      debitValue = "debitValue",
      isSchemePayingCharge = "isSchemePayingCharge",
      schemePaymentElectionDate = "schemePaymentElectionDate",
      schemePayingChargeAmount = "schemePayingChargeAmount",
      schemePayingPstr = "schemePayingPstr2",
      schemePayingName = "schemePayingName2"
    )
  )
}
