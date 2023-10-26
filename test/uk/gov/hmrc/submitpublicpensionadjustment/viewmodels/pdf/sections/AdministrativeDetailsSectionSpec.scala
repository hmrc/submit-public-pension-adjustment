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
import uk.gov.hmrc.submitpublicpensionadjustment.TestData.calculationInputs

class AdministrativeDetailsSectionSpec extends AnyFreeSpec with Matchers with Logging {

  "section must be constructed based on final submission with no resubmissionReason" in {

    val section = AdministrativeDetailsSection.build(TestData.finalSubmission)

    section mustBe AdministrativeDetailsSection(
      firstName = "FirstName",
      surname = "Surname",
      dob = "13/01/1920",
      addressLine1 = "testLine1",
      addressLine2 = "testLine2",
      townOrCity = "TestCity",
      county = Some("TestCounty"),
      stateOrRegion = None,
      postCode = Some("Postcode"),
      postalCode = None,
      country = "United Kingdom",
      utr = "someUtr",
      ninoOrTrn = "someNino",
      contactNumber = "1234567890",
      resubmissionReason = None
    )
  }

  "section must be constructed based on final submission with resubmissionReason" in {

    val section = AdministrativeDetailsSection.build(TestData.finalSubmissionWithResubmissionReason)

    section mustBe AdministrativeDetailsSection(
      firstName = "FirstName",
      surname = "Surname",
      dob = "13/01/1920",
      addressLine1 = "testLine1",
      addressLine2 = "testLine2",
      townOrCity = "TestCity",
      county = Some("TestCounty"),
      stateOrRegion = None,
      postCode = Some("Postcode"),
      postalCode = None,
      country = "United Kingdom",
      utr = "someUtr",
      ninoOrTrn = "someNino",
      contactNumber = "1234567890",
      resubmissionReason = Some("Test resubmission reason")
    )
  }
}
