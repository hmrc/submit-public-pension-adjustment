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

import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.FinalSubmission
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.{Formatting, Section}

case class LifetimeAllowanceSection(
  hadBce: String,
  bceDate: String,
  changeInLtaPercentage: String,
  ltaChargeType: String,
  multipleBenefitCrystallisationEvent: String,
  haveLtaProtectionOrEnhancement: String,
  protectionType: String,
  protectionReference: String,
  enhancementType: String,
  internationalEnhancementReference: String,
  pensionCreditReference: String,
  changeToProtectionType: String,
  newProtectionTypeOrEnhancement: String,
  newProtectionTypeOrReference: String,
  newEnhancementType: String,
  newInternationalEnhancementReference: String,
  newPensionCreditReference: String,
  hadLtaCharge: String,
  howExcessPaid: String,
  lumpSumValue: String,
  annualPaymentValue: String,
  whoPaidLtaCharge: String,
  schemeThatPaidChargeName: String,
  schemeThatPaidChargeTaxRef: String,
  yearChargePaid: String,
  quarterChargePaid: String,
  newExcessLifetimeAllowancePaid: String,
  newLumpSumValue: String,
  newAnnualPaymentValue: String,
  whoPayingExtraCharge: String,
  whoPayingExtraChargeSchemeName: String,
  whoPayingExtraChargeTaxRef: String
) extends Section {
  override def orderedFieldNames(): Seq[String] = Seq(
    "hadBce",
    "bceDate",
    "changeInLtaPercentage",
    "ltaChargeType",
    "multipleBenefitCrystallisationEvent",
    "haveLtaProtectionOrEnhancement",
    "protectionType",
    "protectionReference",
    "enhancementType",
    "internationalEnhancementReference",
    "pensionCreditReference",
    "changeToProtectionType",
    "newProtectionTypeOrEnhancement",
    "newProtectionTypeOrReference",
    "newEnhancementType",
    "newInternationalEnhancementReference",
    "newPensionCreditReference",
    "hadLtaCharge",
    "howExcessPaid",
    "lumpSumValue",
    "annualPaymentValue",
    "whoPaidLtaCharge",
    "schemeThatPaidChargeName",
    "schemeThatPaidChargeTaxRef",
    "yearChargePaid",
    "quarterChargePaid",
    "newExcessLifetimeAllowancePaid",
    "newLumpSumValue",
    "newAnnualPaymentValue",
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
            hadBce = format(finalSubmission.calculationInputs.setup.lifetimeAllowanceSetup match {
              case Some(v) => v.benefitCrystallisationEventFlag.getOrElse(false)
              case _       => false
            }),
            bceDate = format(ltaInputs.benefitCrystallisationEventDate),
            changeInLtaPercentage = format(finalSubmission.calculationInputs.setup.lifetimeAllowanceSetup match {
              case Some(v) => v.changeInLifetimeAllowancePercentageInformedFlag.getOrElse(false)
              case _       => false
            }),
            ltaChargeType = format(ltaInputs.changeInTaxCharge),
            multipleBenefitCrystallisationEvent =
              format(finalSubmission.calculationInputs.setup.lifetimeAllowanceSetup match {
                case Some(v) => v.multipleBenefitCrystallisationEventFlag.getOrElse(false)
                case _       => false
              }),
            haveLtaProtectionOrEnhancement = format(ltaInputs.lifetimeAllowanceProtectionOrEnhancements),
            protectionType = formatProtectionType(ltaInputs.protectionType),
            protectionReference = formatStringNotApplicable(ltaInputs.protectionReference),
            enhancementType = formatEnhancementType(ltaInputs.newLifeTimeAllowanceAdditions.enhancementType),
            internationalEnhancementReference =
              ltaInputs.newLifeTimeAllowanceAdditions.internationalEnhancementReference.getOrElse(NotApplicable),
            pensionCreditReference =
              ltaInputs.newLifeTimeAllowanceAdditions.pensionCreditReference.getOrElse(NotApplicable),
            changeToProtectionType = formatLtaProtectionOrEnhancements(ltaInputs.protectionTypeEnhancementChanged),
            newProtectionTypeOrEnhancement = format(ltaInputs.newProtectionTypeOrEnhancement),
            newProtectionTypeOrReference = ltaInputs.newProtectionTypeOrEnhancementReference.getOrElse(NotApplicable),
            newEnhancementType = formatNewEnhancementType(ltaInputs.newLifeTimeAllowanceAdditions.newEnhancementType),
            newInternationalEnhancementReference =
              ltaInputs.newLifeTimeAllowanceAdditions.newInternationalEnhancementReference.getOrElse(NotApplicable),
            newPensionCreditReference =
              ltaInputs.newLifeTimeAllowanceAdditions.newPensionCreditReference.getOrElse(NotApplicable),
            hadLtaCharge = format(ltaInputs.previousLifetimeAllowanceChargeFlag),
            howExcessPaid = formatExcessLifetimeAllowancePaid(ltaInputs.previousLifetimeAllowanceChargePaymentMethod),
            lumpSumValue = formatOptPoundsAmount(ltaInputs.newLifeTimeAllowanceAdditions.lumpSumValue),
            annualPaymentValue = formatOptPoundsAmount(ltaInputs.newLifeTimeAllowanceAdditions.annualPaymentValue),
            whoPaidLtaCharge = formatWhoPaidLTACharge(ltaInputs.previousLifetimeAllowanceChargePaidBy),
            schemeThatPaidChargeName =
              ltaInputs.previousLifetimeAllowanceChargeSchemeNameAndTaxRef.map(_.name).getOrElse(NotApplicable),
            schemeThatPaidChargeTaxRef =
              ltaInputs.previousLifetimeAllowanceChargeSchemeNameAndTaxRef.map(_.taxRef).getOrElse(NotApplicable),
            yearChargePaid = formatYearChargePaid(ltaInputs.newLifeTimeAllowanceAdditions.yearChargePaid),
            quarterChargePaid = formatQuarterChargePaid(ltaInputs.newLifeTimeAllowanceAdditions.quarterChargePaid),
            newExcessLifetimeAllowancePaid = formatNewExcessLifetimeAllowancePaid(
              ltaInputs.newLifeTimeAllowanceAdditions.newExcessLifetimeAllowancePaid
            ),
            newLumpSumValue = formatOptPoundsAmount(ltaInputs.newLifeTimeAllowanceAdditions.newLumpSumValue),
            newAnnualPaymentValue =
              formatOptPoundsAmount(ltaInputs.newLifeTimeAllowanceAdditions.newAnnualPaymentValue),
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
