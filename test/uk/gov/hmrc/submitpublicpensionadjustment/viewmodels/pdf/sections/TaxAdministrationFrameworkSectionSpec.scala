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

    sections mustBe Seq(
      TaxAdministrationFrameworkSection(
        relatingTo = Period._2017,
        previousChargeAmount = "£300",
        whoChargePaidBy = "Both",
        creditValue = "£200",
        debitValue = "£25",
        isSchemePayingCharge = "Yes",
        schemePaymentElectionDate = "13/01/2017",
        schemePayingChargeAmount = "10",
        schemePayingPstr = "schemePstr",
        schemePayingName = "TestSceme",
        schemeDetailsSubSections = Seq(
          SchemeDetailsSubSection(1, "TestName2017", "TestTaxRef2017"),
          SchemeDetailsSubSection(2, "TestName2222017", "TestTaxRef")
        )
      ),
      TaxAdministrationFrameworkSection(
        relatingTo = Period._2018,
        previousChargeAmount = "£1700",
        whoChargePaidBy = "Scheme",
        creditValue = "£1145076",
        debitValue = "£636",
        isSchemePayingCharge = "No",
        schemePaymentElectionDate = "Not Applicable",
        schemePayingChargeAmount = "Not Applicable",
        schemePayingPstr = "Not Applicable",
        schemePayingName = "Not Applicable",
        schemeDetailsSubSections = Seq(
          SchemeDetailsSubSection(1, "TestName2018", "TestTaxRef"),
          SchemeDetailsSubSection(2, "TestName22018", "TestTaxRef")
        )
      )
    )
  }
}
