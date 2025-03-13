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

class IncomeSubJourneySectionSpec extends AnyFreeSpec with Matchers with Logging {

  "section must be constructed based on final submission" in {

    val sections = IncomeSubJourneySection.build(TestData.finalSubmission)
    sections `mustBe` Seq(
      IncomeSubJourneySection(
        "2018/19",
        IncomeSubJourneySubSection(
          "£100",
          "£50,000",
          "£450",
          "£25"
        )
      ),
      IncomeSubJourneySection(
        "2017/18",
        IncomeSubJourneySubSection(
          "£100",
          "£50,000",
          "£450",
          "£25"
        )
      ),
      IncomeSubJourneySection(
        "2016/17",
        IncomeSubJourneySubSection(
          "£100",
          "£50,000",
          "£450",
          "£25"
        )
      )
    )
  }

  "schemePaidChargeSubSections must be empty when taxYears do not exist" in {

    val sections = IncomeSubJourneySection.build(
      TestData.finalSubmission.copy(TestData.finalSubmission.calculationInputs.copy(annualAllowance = None))
    )
    sections `mustBe` Seq.empty
  }
}
