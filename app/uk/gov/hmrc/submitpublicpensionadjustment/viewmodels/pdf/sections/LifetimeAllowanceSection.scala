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

/*
case class LifeTimeAllowance(
  benefitCrystallisationEventFlag: Boolean,
  benefitCrystallisationEventDate: LocalDate,
  changeInLifetimeAllowancePercentageInformedFlag: Boolean,
  changeInTaxCharge: ChangeInTaxCharge,
  lifetimeAllowanceProtectionOrEnhancements: LtaProtectionOrEnhancements,
  protectionType: ProtectionType,
  protectionReference: String,
  protectionTypeOrEnhancementChangedFlag: Boolean,
  newProtectionTypeOrEnhancement: Option[WhatNewProtectionTypeEnhancement],
  newProtectionTypeOrEnhancementReference: Option[String],
  previousLifetimeAllowanceChargeFlag: Boolean,
  previousLifetimeAllowanceChargePaymentMethod: Option[ExcessLifetimeAllowancePaid],
  previousLifetimeAllowanceChargeAmount: Option[Int],
  previousLifetimeAllowanceChargePaidBy: Option[WhoPaidLTACharge],
  previousLifetimeAllowanceChargeSchemeNameAndTaxRef: Option[SchemeNameAndTaxRef],
  newLifetimeAllowanceChargeAmount: Int,
  newLifetimeAllowanceChargeWillBePaidBy: Option[WhoPayingExtraLtaCharge],
  newLifetimeAllowanceChargeSchemeNameAndTaxRef: Option[LtaPensionSchemeDetails]
)
 */

object LifetimeAllowanceSection {
  def build(finalSubmission: FinalSubmission): Option[LifetimeAllowanceSection] =
    finalSubmission.calculationInputs.lifeTimeAllowance match {
      case Some(ltaInputs) =>
        Some(
          LifetimeAllowanceSection(
            hadBce = "Yes",
            bceDate = Formatting.format(ltaInputs.benefitCrystallisationEventDate),
            changeInLtaPercentage = "Yes",
            ltaChargeType = Formatting.format(ltaInputs.changeInTaxCharge),
            haveLtaProtectionOrEnhancement = Formatting.format(ltaInputs.lifetimeAllowanceProtectionOrEnhancements),
            protectionType = Formatting.format(ltaInputs.protectionType),
            protectionReference = ltaInputs.protectionReference,
            changeToProtectionType = Formatting.format(ltaInputs.protectionTypeOrEnhancementChangedFlag),
            newProtectionTypeOrEnhancement = Formatting.format(ltaInputs.newProtectionTypeOrEnhancement),
            newProtectionTypeOrReference =
              ltaInputs.newProtectionTypeOrEnhancementReference.getOrElse("Not Applicable"),
            hadLtaCharge = Formatting.format(ltaInputs.previousLifetimeAllowanceChargeFlag),
            howExcessPaid =
              Formatting.formatExcessLifetimeAllowancePaid(ltaInputs.previousLifetimeAllowanceChargePaymentMethod),
            ltaChargeAmount =
              ltaInputs.previousLifetimeAllowanceChargeAmount.map(v => s"£$v").getOrElse("Not Applicable"),
            whoPaidLtaCharge = Formatting.formatWhoPaidLTACharge(ltaInputs.previousLifetimeAllowanceChargePaidBy),
            schemeThatPaidChargeName =
              ltaInputs.previousLifetimeAllowanceChargeSchemeNameAndTaxRef.map(_.name).getOrElse("Not Applicable"),
            schemeThatPaidChargeTaxRef =
              ltaInputs.previousLifetimeAllowanceChargeSchemeNameAndTaxRef.map(_.taxRef).getOrElse("Not Applicable"),
            newLtaChargeValue = s"£${ltaInputs.newLifetimeAllowanceChargeAmount}",
            whoPayingExtraCharge =
              Formatting.formatWhoPayingExtraLtaCharge(ltaInputs.newLifetimeAllowanceChargeWillBePaidBy),
            whoPayingExtraChargeSchemeName =
              ltaInputs.newLifetimeAllowanceChargeSchemeNameAndTaxRef.map(_.name).getOrElse("Not Applicable"),
            whoPayingExtraChargeTaxRef =
              ltaInputs.newLifetimeAllowanceChargeSchemeNameAndTaxRef.map(_.taxRef).getOrElse("Not Applicable")
          )
        )
      case _               => None
    }
}
