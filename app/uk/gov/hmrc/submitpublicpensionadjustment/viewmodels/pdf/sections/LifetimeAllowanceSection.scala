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
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.{Formatting, Section}

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
  whoPaidLtaCharge: String,
  schemeThatPaidChargeName: String,
  schemeThatPaidChargeTaxRef: String,
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
    "whoPaidLtaCharge",
    "schemeThatPaidChargeName",
    "schemeThatPaidChargeTaxRef",
    "whoPayingExtraCharge",
    "whoPayingExtraChargeSchemeName",
    "whoPayingExtraChargeTaxRef"
  )
}

object LifetimeAllowanceSection extends Formatting {
  def build(finalSubmission: FinalSubmission): Option[LifetimeAllowanceSection] =
    finalSubmission.calculationInputs.lifeTimeAllowance match {
      case Some(ltaInputs) =>
        Some(
          LifetimeAllowanceSection(
            hadBce = format(true),
            bceDate = format(ltaInputs.benefitCrystallisationEventDate),
            changeInLtaPercentage = format(true),
            ltaChargeType = format(ltaInputs.changeInTaxCharge),
            haveLtaProtectionOrEnhancement = format(ltaInputs.lifetimeAllowanceProtectionOrEnhancements),
            protectionType = format(ltaInputs.protectionType),
            protectionReference = ltaInputs.protectionReference,
            changeToProtectionType = format(ltaInputs.protectionTypeOrEnhancementChangedFlag),
            newProtectionTypeOrEnhancement = format(ltaInputs.newProtectionTypeOrEnhancement),
            newProtectionTypeOrReference = ltaInputs.newProtectionTypeOrEnhancementReference.getOrElse(NotApplicable),
            hadLtaCharge = format(ltaInputs.previousLifetimeAllowanceChargeFlag),
            howExcessPaid = formatExcessLifetimeAllowancePaid(ltaInputs.previousLifetimeAllowanceChargePaymentMethod),
            whoPaidLtaCharge = formatWhoPaidLTACharge(ltaInputs.previousLifetimeAllowanceChargePaidBy),
            schemeThatPaidChargeName =
              ltaInputs.previousLifetimeAllowanceChargeSchemeNameAndTaxRef.map(_.name).getOrElse(NotApplicable),
            schemeThatPaidChargeTaxRef =
              ltaInputs.previousLifetimeAllowanceChargeSchemeNameAndTaxRef.map(_.taxRef).getOrElse(NotApplicable),
            whoPayingExtraCharge = formatWhoPayingExtraLtaCharge(ltaInputs.newLifetimeAllowanceChargeWillBePaidBy),
            whoPayingExtraChargeSchemeName =
              ltaInputs.newLifetimeAllowanceChargeSchemeNameAndTaxRef.map(_.name).getOrElse(NotApplicable),
            whoPayingExtraChargeTaxRef =
              ltaInputs.newLifetimeAllowanceChargeSchemeNameAndTaxRef.map(_.taxRef).getOrElse(NotApplicable)
          )
        )
      case _               => None
    }
}
