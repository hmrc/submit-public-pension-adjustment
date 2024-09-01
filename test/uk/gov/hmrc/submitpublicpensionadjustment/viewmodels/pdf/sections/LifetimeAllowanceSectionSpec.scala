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

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.Logging
import uk.gov.hmrc.submitpublicpensionadjustment.TestData
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.{AnnualAllowanceSetup, CalculationInputs, ExcessLifetimeAllowancePaid, LifetimeAllowanceSetup, Resubmission => inputsResubmission, Setup, WhoPaidLTACharge, WhoPayingExtraLtaCharge}
import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.FinalSubmission

class LifetimeAllowanceSectionSpec extends AnyFreeSpec with Matchers with Logging {

  "section must be constructed based on final submission" in {
    val calculationInputs = CalculationInputs(
      inputsResubmission(false, None),
      Setup(Some(AnnualAllowanceSetup(Some(true))), Some(LifetimeAllowanceSetup(Some(true), Some(true), Some(false)))),
      None,
      Some(TestData.lifeTimeAllowance)
    )

    val finalSubmission = FinalSubmission(calculationInputs, None, TestData.submissionInputs)

    val ltaSection: LifetimeAllowanceSection = LifetimeAllowanceSection.build(finalSubmission).get

    ltaSection.hadBce mustBe "Yes"
    ltaSection.bceDate mustBe "30/01/2017"
    ltaSection.changeInLtaPercentage mustBe "Yes"
    ltaSection.ltaChargeType mustBe "New"
    ltaSection.multipleBenefitCrystallisationEvent mustBe "No"
    ltaSection.haveLtaProtectionOrEnhancement mustBe "Protection"
    ltaSection.protectionType mustBe "Primary protection"
    ltaSection.protectionReference mustBe "originalReference"
    ltaSection.enhancementType mustBe "Both"
    ltaSection.internationalEnhancementReference mustBe "internationalRef"
    ltaSection.pensionCreditReference mustBe "pensionCredRef"
    ltaSection.changeToProtectionType mustBe "Protection"
    ltaSection.newProtectionTypeOrEnhancement mustBe "Enhanced protection"
    ltaSection.newProtectionTypeOrReference mustBe "newReference"
    ltaSection.newEnhancementType mustBe "Both"
    ltaSection.newInternationalEnhancementReference mustBe "newinternationEnhancementRef"
    ltaSection.newPensionCreditReference mustBe "newPensionCredRef"
    ltaSection.hadLtaCharge mustBe "Yes"
    ltaSection.howExcessPaid mustBe "Lump Sum"
    ltaSection.lumpSumValue mustBe "£5"
    ltaSection.annualPaymentValue mustBe "£6"
    ltaSection.whoPaidLtaCharge mustBe "Scheme"
    ltaSection.schemeThatPaidChargeName mustBe "Scheme1"
    ltaSection.schemeThatPaidChargeTaxRef mustBe "pstr1"
    ltaSection.yearChargePaid mustBe "6 April 2015 to 5 April 2016"
    ltaSection.quarterChargePaid mustBe "1 April to 30 June"
    ltaSection.newExcessLifetimeAllowancePaid mustBe "Both"
    ltaSection.newLumpSumValue mustBe "£7"
    ltaSection.newAnnualPaymentValue mustBe "£8"
    ltaSection.whoPayingExtraCharge mustBe "Scheme"
    ltaSection.whoPayingExtraChargeSchemeName mustBe "Scheme2"
    ltaSection.whoPayingExtraChargeTaxRef mustBe "pstr2"
  }

  "formatExcessLifetimeAllowancePaid to Annual Payment when ExcessLifetimeAllowancePaid.Annualpayment" in {
    val calculationInputs = CalculationInputs(
      inputsResubmission(false, None),
      Setup(Some(AnnualAllowanceSetup(Some(true))), Some(LifetimeAllowanceSetup(Some(true), Some(true), Some(false)))),
      None,
      Some(
        TestData.lifeTimeAllowance.copy(previousLifetimeAllowanceChargePaymentMethod =
          Some(ExcessLifetimeAllowancePaid.Annualpayment)
        )
      )
    )

    val finalSubmission = FinalSubmission(calculationInputs, None, TestData.submissionInputs)

    val ltaSection: LifetimeAllowanceSection = LifetimeAllowanceSection.build(finalSubmission).get

    ltaSection.howExcessPaid mustBe "Annual Payment"
  }

  "formatExcessLifetimeAllowancePaid to Both when ExcessLifetimeAllowancePaid.Both" in {
    val calculationInputs = CalculationInputs(
      inputsResubmission(false, None),
      Setup(Some(AnnualAllowanceSetup(Some(true))), Some(LifetimeAllowanceSetup(Some(true), Some(true), Some(false)))),
      None,
      Some(
        TestData.lifeTimeAllowance.copy(previousLifetimeAllowanceChargePaymentMethod =
          Some(ExcessLifetimeAllowancePaid.Both)
        )
      )
    )

    val finalSubmission = FinalSubmission(calculationInputs, None, TestData.submissionInputs)

    val ltaSection: LifetimeAllowanceSection = LifetimeAllowanceSection.build(finalSubmission).get

    ltaSection.howExcessPaid mustBe "Both"
  }

  "formatExcessLifetimeAllowancePaid to NotApplicable when ExcessLifetimeAllowancePaid doesn't exist" in {
    val calculationInputs = CalculationInputs(
      inputsResubmission(false, None),
      Setup(Some(AnnualAllowanceSetup(Some(true))), Some(LifetimeAllowanceSetup(Some(true), Some(true), Some(false)))),
      None,
      Some(TestData.lifeTimeAllowance.copy(previousLifetimeAllowanceChargePaymentMethod = None))
    )

    val finalSubmission = FinalSubmission(calculationInputs, None, TestData.submissionInputs)

    val ltaSection: LifetimeAllowanceSection = LifetimeAllowanceSection.build(finalSubmission).get

    ltaSection.howExcessPaid mustBe "Not Applicable"
  }

  "formatWhoPaidLTACharge to Member when WhoPaidLTACharge.You" in {
    val calculationInputs = CalculationInputs(
      inputsResubmission(false, None),
      Setup(Some(AnnualAllowanceSetup(Some(true))), Some(LifetimeAllowanceSetup(Some(true), Some(true), Some(false)))),
      None,
      Some(TestData.lifeTimeAllowance.copy(previousLifetimeAllowanceChargePaidBy = Some(WhoPaidLTACharge.You)))
    )

    val finalSubmission = FinalSubmission(calculationInputs, None, TestData.submissionInputs)

    val ltaSection: LifetimeAllowanceSection = LifetimeAllowanceSection.build(finalSubmission).get

    ltaSection.whoPaidLtaCharge mustBe "Member"
  }

  "formatWhoPaidLTACharge to Not Applicable when WhoPaidLTACharge doesn't exist" in {
    val calculationInputs = CalculationInputs(
      inputsResubmission(false, None),
      Setup(Some(AnnualAllowanceSetup(Some(true))), Some(LifetimeAllowanceSetup(Some(true), Some(true), Some(false)))),
      None,
      Some(TestData.lifeTimeAllowance.copy(previousLifetimeAllowanceChargePaidBy = None))
    )

    val finalSubmission = FinalSubmission(calculationInputs, None, TestData.submissionInputs)

    val ltaSection: LifetimeAllowanceSection = LifetimeAllowanceSection.build(finalSubmission).get

    ltaSection.whoPaidLtaCharge mustBe "Not Applicable"
  }

  "formatWhoPayingExtraLtaCharge to Member when WhoPayingExtraLtaCharge.You" in {
    val calculationInputs = CalculationInputs(
      inputsResubmission(false, None),
      Setup(Some(AnnualAllowanceSetup(Some(true))), Some(LifetimeAllowanceSetup(Some(true), Some(true), Some(false)))),
      None,
      Some(TestData.lifeTimeAllowance.copy(newLifetimeAllowanceChargeWillBePaidBy = Some(WhoPayingExtraLtaCharge.You)))
    )

    val finalSubmission = FinalSubmission(calculationInputs, None, TestData.submissionInputs)

    val ltaSection: LifetimeAllowanceSection = LifetimeAllowanceSection.build(finalSubmission).get

    ltaSection.whoPayingExtraCharge mustBe "Member"
  }

  "formatWhoPayingExtraLtaCharge to Not Applicable when WhoPayingExtraLtaCharge does not exist" in {
    val calculationInputs = CalculationInputs(
      inputsResubmission(false, None),
      Setup(Some(AnnualAllowanceSetup(Some(true))), Some(LifetimeAllowanceSetup(Some(true), Some(true), Some(false)))),
      None,
      Some(TestData.lifeTimeAllowance.copy(newLifetimeAllowanceChargeWillBePaidBy = None))
    )

    val finalSubmission = FinalSubmission(calculationInputs, None, TestData.submissionInputs)

    val ltaSection: LifetimeAllowanceSection = LifetimeAllowanceSection.build(finalSubmission).get

    ltaSection.whoPayingExtraCharge mustBe "Not Applicable"
  }

  "section must be constructed with Not Applicable when row not present" in {
    val calculationInputs = CalculationInputs(
      inputsResubmission(false, None),
      Setup(Some(AnnualAllowanceSetup(Some(true))), Some(LifetimeAllowanceSetup(Some(true), Some(true), Some(true)))),
      None,
      Some(
        TestData.lifeTimeAllowance.copy(
          newProtectionTypeOrEnhancementReference = None,
          previousLifetimeAllowanceChargeSchemeNameAndTaxRef = None,
          newLifetimeAllowanceChargeSchemeNameAndTaxRef = None,
          newLifeTimeAllowanceAdditions = TestData.newLifeTimeAllowanceAdditions.copy(
            internationalEnhancementReference = None,
            pensionCreditReference = None,
            newInternationalEnhancementReference = None,
            newPensionCreditReference = None
          )
        )
      )
    )

    val finalSubmission = FinalSubmission(calculationInputs, None, TestData.submissionInputs)

    val ltaSection: LifetimeAllowanceSection = LifetimeAllowanceSection.build(finalSubmission).get

    ltaSection.hadBce mustBe "Yes"
    ltaSection.bceDate mustBe "30/01/2017"
    ltaSection.changeInLtaPercentage mustBe "Yes"
    ltaSection.ltaChargeType mustBe "New"
    ltaSection.multipleBenefitCrystallisationEvent mustBe "Yes"
    ltaSection.haveLtaProtectionOrEnhancement mustBe "Protection"
    ltaSection.protectionType mustBe "Primary protection"
    ltaSection.protectionReference mustBe "originalReference"
    ltaSection.enhancementType mustBe "Both"
    ltaSection.internationalEnhancementReference mustBe "Not Applicable"
    ltaSection.pensionCreditReference mustBe "Not Applicable"
    ltaSection.changeToProtectionType mustBe "Protection"
    ltaSection.newProtectionTypeOrEnhancement mustBe "Enhanced protection"
    ltaSection.newProtectionTypeOrReference mustBe "Not Applicable"
    ltaSection.newEnhancementType mustBe "Both"
    ltaSection.newInternationalEnhancementReference mustBe "Not Applicable"
    ltaSection.newPensionCreditReference mustBe "Not Applicable"
    ltaSection.hadLtaCharge mustBe "Yes"
    ltaSection.howExcessPaid mustBe "Lump Sum"
    ltaSection.lumpSumValue mustBe "£5"
    ltaSection.annualPaymentValue mustBe "£6"
    ltaSection.whoPaidLtaCharge mustBe "Scheme"
    ltaSection.schemeThatPaidChargeName mustBe "Not Applicable"
    ltaSection.schemeThatPaidChargeTaxRef mustBe "Not Applicable"
    ltaSection.yearChargePaid mustBe "6 April 2015 to 5 April 2016"
    ltaSection.quarterChargePaid mustBe "1 April to 30 June"
    ltaSection.newExcessLifetimeAllowancePaid mustBe "Both"
    ltaSection.newLumpSumValue mustBe "£7"
    ltaSection.newAnnualPaymentValue mustBe "£8"
    ltaSection.whoPayingExtraCharge mustBe "Scheme"
    ltaSection.whoPayingExtraChargeSchemeName mustBe "Not Applicable"
    ltaSection.whoPayingExtraChargeTaxRef mustBe "Not Applicable"
  }

  "Must be none when ltaInputs does not exist" in {
    val calculationInputs = CalculationInputs(
      inputsResubmission(false, None),
      Setup(Some(AnnualAllowanceSetup(Some(true))), Some(LifetimeAllowanceSetup(Some(true), Some(true), Some(false)))),
      None,
      None
    )

    val finalSubmission = FinalSubmission(calculationInputs, None, TestData.submissionInputs)

    val ltaSection: Option[LifetimeAllowanceSection] = LifetimeAllowanceSection.build(finalSubmission)

    ltaSection mustBe None
  }

}
