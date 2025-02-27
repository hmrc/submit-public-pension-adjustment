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
          SchemePaidChargeDetailsSubSection(1, 100, "TestName2017", "TestTaxRef2017", 991),
          SchemePaidChargeDetailsSubSection(2, 100, "TestName2222017", "TestTaxRef", 991)
        )
      ),
      CompensationSection(
        Period.Year(2018),
        "£1,002,018",
        "£2,002,018",
        "£2,702,018",
        "£502,018",
        Seq(
          SchemePaidChargeDetailsSubSection(1, 100, "TestName2018", "TestTaxRef", 991),
          SchemePaidChargeDetailsSubSection(2, 100, "TestName22018", "TestTaxRef", 991)
        )
      ),
      CompensationSection(
        Period.Year(2019),
        "£1,002,019",
        "£2,002,019",
        "£2,702,019",
        "£502,019",
        Seq(
          SchemePaidChargeDetailsSubSection(1, 100, "TestName2019", "TestTaxRef", 991),
          SchemePaidChargeDetailsSubSection(2, 100, "TestName22019", "TestTaxRef", 991)
        )
      )
    )
  }

  "section sequence must be empty when outDates does not exist" in {

    val sections = CompensationSection.build(TestData.finalSubmission.copy(calculation = None))

    sections mustBe Seq.empty
  }

  "schemePaidChargeSubSections must be empty when taxYears do not exist" in {

    val sections = CompensationSection.build(
      TestData.finalSubmission.copy(TestData.finalSubmission.calculationInputs.copy(annualAllowance = None))
    )
    sections mustBe Seq(
      CompensationSection(
        relatingTo = Period._2017,
        directAmount = "£100",
        indirectAmount = "£200",
        revisedTaxChargeTotal = "£270",
        chargeYouPaid = "£50",
        schemePaidChargeSubSections = Seq(
        )
      ),
      CompensationSection(
        Period.Year(2018),
        "£1,002,018",
        "£2,002,018",
        "£2,702,018",
        "£502,018",
        Seq(
        )
      ),
      CompensationSection(
        Period.Year(2019),
        "£1,002,019",
        "£2,002,019",
        "£2,702,019",
        "£502,019",
        Seq(
        )
      )
    )
  }
}
