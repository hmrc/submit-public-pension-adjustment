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

import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.FinalSubmission
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.Section

case class LifetimeAllowanceSection(
  hadBce: String,
  bceDate: String,
  changeInLtaPercentage: String,
  ltaChargeType: String,
  haveLtaProtectionOrEnhancement: String,
  protectionType: String,
  protectionReference: String,
  changeToProtectionType: String,
  newProtectionTypeOrEnhancement: String,
  newProtectionTypeOrReference: String,
  hadLtaCharge: String,
  howExcessPaid: String,
  ltaChargeAmount: String,
  whoPaidLtaCharge: String,
  schemeThatPaidChargeName: String,
  schemeThatPaidChargeTaxRef: String,
  newLtaChargeValue: String,
  whoPayingExtraCharge: String,
  whoPayingExtraChargeSchemeName: String,
  whoPayingExtraChargeTaxRef: String
) extends Section {
  override def orderedFieldNames(): Seq[String] = Seq(
    "hadBce",
    "bceDate",
    "changeInLtaPercentage",
    "ltaChargeType",
    "haveLtaProtectionOrEnhancement",
    "protectionType",
    "protectionReference",
    "changeToProtectionType",
    "newProtectionTypeOrEnhancement",
    "newProtectionTypeOrReference",
    "hadLtaCharge",
    "howExcessPaid",
    "ltaChargeAmount",
    "whoPaidLtaCharge",
    "schemeThatPaidChargeName",
    "schemeThatPaidChargeTaxRef",
    "newLtaChargeValue",
    "whoPayingExtraCharge",
    "whoPayingExtraChargeSchemeName",
    "whoPayingExtraChargeTaxRef"
  )
}

object LifetimeAllowanceSection {
  def build(finalSubmission: FinalSubmission): Option[LifetimeAllowanceSection] =
    finalSubmission.calculationInputs.lifeTimeAllowance match {
      case Some(_) =>
        Some(
          LifetimeAllowanceSection(
            hadBce = "todo",
            bceDate = "todo",
            changeInLtaPercentage = "todo",
            ltaChargeType = "todo",
            haveLtaProtectionOrEnhancement = "todo",
            protectionType = "todo",
            protectionReference = "todo",
            changeToProtectionType = "todo",
            newProtectionTypeOrEnhancement = "todo",
            newProtectionTypeOrReference = "todo",
            hadLtaCharge = "todo",
            howExcessPaid = "todo",
            ltaChargeAmount = "todo",
            whoPaidLtaCharge = "todo",
            schemeThatPaidChargeName = "todo",
            schemeThatPaidChargeTaxRef = "todo",
            newLtaChargeValue = "todo",
            whoPayingExtraCharge = "todo",
            whoPayingExtraChargeSchemeName = "todo",
            whoPayingExtraChargeTaxRef = "todo"
          )
        )
      case _       => None
    }
}
