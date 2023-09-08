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
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.response.Period

class CompensationSectionSpec extends AnyFreeSpec with Matchers with Logging {

  "section must be constructed based on final submission" in {

    val sections = CompensationSection.build(TestData.finalSubmission)

    sections mustBe Seq(
      CompensationSection(
        relatingTo = Period._2017,
        directAmount = "£100",
        indirectAmount = "£200",
        revisedTaxChargeTotal = "£270",
        chargeYouPaid = "£50",
        schemePaidChargeSubSections = Seq(
          SchemePaidChargeDetailsSubSection(1, 100, "TestName2017", "TestTaxRef2017"),
          SchemePaidChargeDetailsSubSection(2, 100, "TestName2222017", "TestTaxRef")
        )
      ),
      CompensationSection(
        Period.Year(2018),
        "£1002018",
        "£2002018",
        "£2702018",
        "£502018",
        Seq(
          SchemePaidChargeDetailsSubSection(1, 100, "TestName2018", "TestTaxRef"),
          SchemePaidChargeDetailsSubSection(2, 100, "TestName22018", "TestTaxRef")
        )
      )
    )
  }
}
