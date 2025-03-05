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

package uk.gov.hmrc.submitpublicpensionadjustment.models.dms

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.submitpublicpensionadjustment.TestData
import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.FinalSubmission

class DmsQueueSpec extends AnyFlatSpec with Matchers {

  "MiniRegime" should "return true if not a resubmission and mini regime is required" in {
    val miniRegime      = MiniRegime("MiniRegimeQueue")
    val finalSubmission = FinalSubmission(
      TestData.calculationInputs,
      Some(TestData.calculationResponse.copy(inDates = List(TestData.inDatesCalculation2019))),
      TestData.submissionInputs
    )

    miniRegime.isRequired(finalSubmission) `shouldBe` true
    miniRegime.precedence `shouldBe` 1
  }

  it should "return false if a resubmission" in {
    val miniRegime      = MiniRegime("MiniRegimeQueue")
    val finalSubmission = FinalSubmission(
      TestData.calculationInputsWithResubmissionReason,
      Some(TestData.calculationResponse.copy(inDates = List(TestData.inDatesCalculation2019))),
      TestData.submissionInputs
    )

    miniRegime.isRequired(finalSubmission) `shouldBe` false
  }

  "MiniRegimeAmendment" should "return true if a resubmission and mini regime is required" in {
    val miniRegimeAmendment = MiniRegimeAmendment("MiniRegimeAmendmentQueue")
    val finalSubmission     = FinalSubmission(
      TestData.calculationInputsWithResubmissionReason,
      Some(TestData.calculationResponse.copy(inDates = List(TestData.inDatesCalculation2019))),
      TestData.submissionInputs
    )

    miniRegimeAmendment.isRequired(finalSubmission) `shouldBe` true
    miniRegimeAmendment.precedence `shouldBe` 2
  }

  it should "return false if not a resubmission" in {
    val miniRegimeAmendment = MiniRegimeAmendment("MiniRegimeAmendmentQueue")
    val finalSubmission     = FinalSubmission(
      TestData.calculationInputs,
      Some(TestData.calculationResponse.copy(inDates = List(TestData.inDatesCalculation2019))),
      TestData.submissionInputs
    )

    miniRegimeAmendment.isRequired(finalSubmission) `shouldBe` false
  }

  it should "return false when mini regime is not required" in {
    val miniRegimeAmendment = MiniRegimeAmendment("MiniRegimeAmendmentQueue")
    val finalSubmission     = FinalSubmission(
      TestData.calculationInputs,
      None,
      TestData.submissionInputs
    )

    miniRegimeAmendment.miniRegimeIsRequired(finalSubmission) `shouldBe` false
  }

  "Compensation" should "return true if not a resubmission and compensation is required" in {
    val compensation    = Compensation("CompensationQueue")
    val finalSubmission = FinalSubmission(
      TestData.calculationInputs,
      Some(TestData.calculationResponse.copy(outDates = List(TestData.outOfDatesCalculation2019))),
      TestData.submissionInputs
    )

    compensation.isRequired(finalSubmission) `shouldBe` true
    compensation.precedence `shouldBe` 3
  }

  it should "return false if a resubmission" in {
    val compensation    = Compensation("CompensationQueue")
    val finalSubmission = FinalSubmission(
      TestData.calculationInputsWithResubmissionReason,
      Some(TestData.calculationResponse.copy(outDates = List(TestData.outOfDatesCalculation2019))),
      TestData.submissionInputs
    )

    compensation.isRequired(finalSubmission) `shouldBe` false
  }

  "CompensationAmendment" should "return true if a resubmission and compensation is required" in {
    val compensationAmendment = CompensationAmendment("CompensationAmendmentQueue")
    val finalSubmission       = FinalSubmission(
      TestData.calculationInputsWithResubmissionReason,
      Some(TestData.calculationResponse.copy(outDates = List(TestData.outOfDatesCalculation2019))),
      TestData.submissionInputs
    )

    compensationAmendment.isRequired(finalSubmission) `shouldBe` true
    compensationAmendment.precedence `shouldBe` 4

  }

  it should "return false when compensation is not required" in {
    val compensationAmendment = CompensationAmendment("CompensationAmendmentQueue")
    val finalSubmission       = FinalSubmission(
      TestData.calculationInputs,
      None,
      TestData.submissionInputs
    )

    compensationAmendment.compensationIsRequired(finalSubmission) `shouldBe` false
  }

  it should "return false if not a resubmission" in {
    val compensationAmendment = CompensationAmendment("CompensationAmendmentQueue")
    val finalSubmission       = FinalSubmission(
      TestData.calculationInputs,
      Some(TestData.calculationResponse.copy(outDates = List(TestData.outOfDatesCalculation2019))),
      TestData.submissionInputs
    )

    compensationAmendment.isRequired(finalSubmission) `shouldBe` false
  }

  "LTA" should "return true if life time allowance is defined" in {
    val lta             = LTA("LTAQueue")
    val finalSubmission = FinalSubmission(
      TestData.calculationInputs.copy(lifeTimeAllowance = Some(TestData.lifeTimeAllowance)),
      None,
      TestData.submissionInputs
    )

    lta.isRequired(finalSubmission) `shouldBe` true
    lta.precedence `shouldBe` 5
  }

  it should "return false if life time allowance is not defined" in {
    val lta             = LTA("LTAQueue")
    val finalSubmission = FinalSubmission(
      TestData.calculationInputs.copy(lifeTimeAllowance = None),
      None,
      TestData.submissionInputs
    )

    lta.isRequired(finalSubmission) `shouldBe` false
  }

}
