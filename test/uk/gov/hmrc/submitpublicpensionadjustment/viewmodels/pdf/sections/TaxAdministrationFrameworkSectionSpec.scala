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
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.response.Period

class TaxAdministrationFrameworkSectionSpec extends AnyFreeSpec with Matchers with Logging {

  "section must be constructed based on final submission" in {

    val sections = TaxAdministrationFrameworkSection.build(TestData.finalSubmission)

    sections `mustBe` Seq(
      TaxAdministrationFrameworkSection(
        relatingTo = Period._2017,
        previousChargeAmount = "£300",
        whoChargePaidBy = "Both",
        creditValue = "£200",
        debitValue = "£25",
        isSchemePayingCharge = "Yes",
        schemePaymentElectionDate = "13/01/2017",
        schemePayingChargeAmount = "£10",
        schemePayingPstr = "schemePstr",
        schemePayingName = "TestSceme",
        schemeDetailsSubSections = Seq(
          SchemeDetailsSubSection(1, "TestName2017", "TestTaxRef2017"),
          SchemeDetailsSubSection(2, "TestName2222017", "TestTaxRef")
        )
      ),
      TaxAdministrationFrameworkSection(
        relatingTo = Period._2018,
        previousChargeAmount = "£1,700",
        whoChargePaidBy = "Scheme",
        creditValue = "£1,145,076",
        debitValue = "£636",
        isSchemePayingCharge = "Yes",
        schemePaymentElectionDate = "Not Applicable",
        schemePayingChargeAmount = "£10",
        schemePayingPstr = "schemePstr",
        schemePayingName = "TestSceme",
        schemeDetailsSubSections = Seq(
          SchemeDetailsSubSection(1, "TestName2018", "TestTaxRef"),
          SchemeDetailsSubSection(2, "TestName22018", "TestTaxRef")
        )
      ),
      TaxAdministrationFrameworkSection(
        relatingTo = Period._2019,
        previousChargeAmount = "£1,700",
        whoChargePaidBy = "Member",
        creditValue = "£200",
        debitValue = "£25",
        isSchemePayingCharge = "No",
        schemePaymentElectionDate = "Not Applicable",
        schemePayingChargeAmount = "Not Applicable",
        schemePayingPstr = "Not Applicable",
        schemePayingName = "Not Applicable",
        schemeDetailsSubSections = Seq(
          SchemeDetailsSubSection(1, "TestName2019", "TestTaxRef"),
          SchemeDetailsSubSection(2, "TestName22019", "TestTaxRef")
        )
      )
    )
  }

  "section must be constructed empty with an empty calculation" in {
    val sections = TaxAdministrationFrameworkSection.build(TestData.finalSubmission.copy(calculation = None))

    sections `mustBe` Seq.empty
  }

  "section must be constructed with an empty relevantTaxYear" in {

    val sections = TaxAdministrationFrameworkSection.build(
      TestData.finalSubmission.copy(TestData.calculationInputs.copy(annualAllowance = None))
    )

    sections `mustBe` Seq(
      TaxAdministrationFrameworkSection(
        relatingTo = Period._2017,
        previousChargeAmount = "£0",
        whoChargePaidBy = "Both",
        creditValue = "£200",
        debitValue = "£25",
        isSchemePayingCharge = "Yes",
        schemePaymentElectionDate = "13/01/2017",
        schemePayingChargeAmount = "£10",
        schemePayingPstr = "schemePstr",
        schemePayingName = "TestSceme",
        schemeDetailsSubSections = Seq()
      ),
      TaxAdministrationFrameworkSection(
        relatingTo = Period._2018,
        previousChargeAmount = "£0",
        whoChargePaidBy = "Scheme",
        creditValue = "£1,145,076",
        debitValue = "£636",
        isSchemePayingCharge = "Yes",
        schemePaymentElectionDate = "Not Applicable",
        schemePayingChargeAmount = "£10",
        schemePayingPstr = "schemePstr",
        schemePayingName = "TestSceme",
        schemeDetailsSubSections = Seq()
      ),
      TaxAdministrationFrameworkSection(
        relatingTo = Period._2019,
        previousChargeAmount = "£0",
        whoChargePaidBy = "Member",
        creditValue = "£200",
        debitValue = "£25",
        isSchemePayingCharge = "No",
        schemePaymentElectionDate = "Not Applicable",
        schemePayingChargeAmount = "Not Applicable",
        schemePayingPstr = "Not Applicable",
        schemePayingName = "Not Applicable",
        schemeDetailsSubSections = Seq()
      )
    )
  }

  "section must be constructed with no inDateCalc" in {

    val sections = TaxAdministrationFrameworkSection.build(
      TestData.finalSubmission.copy(calculation =
        Some(
          TestData.calculationResponse.copy(inDates =
            List(TestData.inDatesCalculation2019.copy(chargePaidByMember = 0, chargePaidBySchemes = 0))
          )
        )
      )
    )

    sections `mustBe` Seq(
      TaxAdministrationFrameworkSection(
        relatingTo = Period._2019,
        previousChargeAmount = "£1,700",
        whoChargePaidBy = "None",
        creditValue = "£200",
        debitValue = "£25",
        isSchemePayingCharge = "No",
        schemePaymentElectionDate = "Not Applicable",
        schemePayingChargeAmount = "Not Applicable",
        schemePayingPstr = "Not Applicable",
        schemePayingName = "Not Applicable",
        schemeDetailsSubSections = List(
          SchemeDetailsSubSection(1, "TestName2019", "TestTaxRef"),
          SchemeDetailsSubSection(2, "TestName22019", "TestTaxRef")
        )
      )
    )
  }

}
