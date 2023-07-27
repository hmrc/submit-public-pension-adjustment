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

  // TODO - Need to map values from final submission.
  def build(finalSubmission: FinalSubmission): Option[LifetimeAllowanceSection] = Some(
    LifetimeAllowanceSection(
      hadBce = "hadBce",
      bceDate = "bceDate",
      changeInLtaPercentage = "changeInLtaPercentage",
      ltaChargeType = "ltaChargeType",
      haveLtaProtectionOrEnhancement = "haveLtaProtectionOrEnhancement",
      protectionType = "protectionType",
      protectionReference = "protectionReference",
      changeToProtectionType = "changeToProtectionType",
      newProtectionTypeOrEnhancement = "newProtectionTypeOrEnhancement",
      newProtectionTypeOrReference = "newProtectionTypeOrReference",
      hadLtaCharge = "hadLtaCharge",
      howExcessPaid = "howExcessPaid",
      ltaChargeAmount = "ltaChargeAmount",
      whoPaidLtaCharge = "whoPaidLtaCharge",
      schemeThatPaidChargeName = "schemeThatPaidChargeName",
      schemeThatPaidChargeTaxRef = "schemeThatPaidChargeTaxRef",
      newLtaChargeValue = "newLtaChargeValue",
      whoPayingExtraCharge = "whoPayingExtraCharge",
      whoPayingExtraChargeSchemeName = "whoPayingExtraChargeSchemeName",
      whoPayingExtraChargeTaxRef = "whoPayingExtraChargeTaxRef"
    )
  )
}
