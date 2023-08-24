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

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.Logging
import uk.gov.hmrc.submitpublicpensionadjustment.TestData
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.{CalculationInputs, Resubmission => inputsResubmission}
import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.FinalSubmission

class LifetimeAllowanceSectionSpec extends AnyFreeSpec with Matchers with Logging {

  "must build LTA section based on calculation inputs" in {
    val calculationInputs = CalculationInputs(
      inputsResubmission(false, None),
      None,
      Some(TestData.lifeTimeAllowance)
    )

    val finalSubmission = FinalSubmission(calculationInputs, None, TestData.submissionInputs)

    val ltaSection: LifetimeAllowanceSection = LifetimeAllowanceSection.build(finalSubmission).get

    ltaSection.hadBce mustBe "Yes"
    ltaSection.bceDate mustBe "30/01/2017"
    ltaSection.changeInLtaPercentage mustBe "Yes"
    ltaSection.ltaChargeType mustBe "New"
    ltaSection.haveLtaProtectionOrEnhancement mustBe "protection"
    ltaSection.protectionType mustBe "primaryProtection"
    ltaSection.protectionReference mustBe ""
    ltaSection.changeToProtectionType mustBe ""
    ltaSection.newProtectionTypeOrEnhancement mustBe ""
    ltaSection.newProtectionTypeOrReference mustBe ""
    ltaSection.hadLtaCharge mustBe ""
    ltaSection.howExcessPaid mustBe ""
    ltaSection.ltaChargeAmount mustBe ""
    ltaSection.whoPaidLtaCharge mustBe ""
    ltaSection.schemeThatPaidChargeName mustBe ""
    ltaSection.schemeThatPaidChargeTaxRef mustBe ""
    ltaSection.newLtaChargeValue mustBe ""
    ltaSection.whoPayingExtraCharge mustBe ""
    ltaSection.whoPayingExtraChargeSchemeName mustBe ""
    ltaSection.whoPayingExtraChargeTaxRef mustBe ""
  }
}
