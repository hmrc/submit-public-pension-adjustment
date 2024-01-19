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
import uk.gov.hmrc.submitpublicpensionadjustment.TestData.calculationInputs
import uk.gov.hmrc.submitpublicpensionadjustment.models.{InternationalAddress, UkAddress}

import java.time.LocalDate

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

  "when constructing 'dob'" - {
    "should return formatted date if dateOfBirth is present" in {
      val finalSubmissionWithDOB = TestData.finalSubmission.copy(
        submissionInputs = TestData.submissionInputs.copy(
          administrativeDetails = TestData.administrativeDetails.copy(
            claimantDetails = TestData.administrativeDetails.claimantDetails.copy(
              claimantPersonalDetails = TestData.administrativeDetails.claimantDetails.claimantPersonalDetails.copy(
                dateOfBirth = Some(LocalDate.of(1980, 1, 1))
              )
            )
          )
        )
      )
      val section = AdministrativeDetailsSection.build(finalSubmissionWithDOB)
      section.dob mustBe "01/01/1980" // Assuming format method is dd/MM/yyyy
    }

    "should return 'NotEntered' if dateOfBirth is None" in {
      val finalSubmissionWithoutDOB = TestData.finalSubmission.copy(
        submissionInputs = TestData.submissionInputs.copy(
          administrativeDetails = TestData.administrativeDetails.copy(
            claimantDetails = TestData.administrativeDetails.claimantDetails.copy(
              claimantPersonalDetails = TestData.administrativeDetails.claimantDetails.claimantPersonalDetails.copy(
                dateOfBirth = None
              )
            )
          )
        )
      )
      val section = AdministrativeDetailsSection.build(finalSubmissionWithoutDOB)
      section.dob mustBe "Not Entered"
    }
  }

  "when constructing address lines" - {
    "should use international address when present" in {
      val internationalAddress = InternationalAddress("Intl Address Line 1", Some("Intl Address Line 2"), "Intl City", Some("Intl Region"), Some("Postalcode"), "FR")
      val personalDetailsWithIntlAddress = TestData.administrativeDetails.claimantDetails.copy(
        claimantPersonalDetails = TestData.administrativeDetails.claimantDetails.claimantPersonalDetails.copy(
          address = None,
          internationalAddress = Some(internationalAddress)
        )
      )
      val submissionWithIntlAddress = TestData.finalSubmission.copy(
        submissionInputs = TestData.submissionInputs.copy(
          administrativeDetails = TestData.administrativeDetails.copy(
            claimantDetails = personalDetailsWithIntlAddress
          )
        )
      )

      val section = AdministrativeDetailsSection.build(submissionWithIntlAddress)
      section.addressLine1 mustBe "Intl Address Line 1"
      section.addressLine2 mustBe "Intl Address Line 2"
      section.townOrCity mustBe "Intl City"
      section.county mustBe None
      section.stateOrRegion mustBe Some("Intl Region")
      section.postCode mustBe None
      section.postalCode mustBe Some("Postalcode")
      section.country mustBe "FR"


    }

    "should return 'NotEntered' when both address and internationalAddress are not present" in {
      val personalDetailsWithoutIntlAddress = TestData.administrativeDetails.claimantDetails.copy(
        claimantPersonalDetails = TestData.administrativeDetails.claimantDetails.claimantPersonalDetails.copy(
          address = None,
          internationalAddress = None
        )
      )
      val submissionWithoutIntlAddress = TestData.finalSubmission.copy(
        submissionInputs = TestData.submissionInputs.copy(
          administrativeDetails = TestData.administrativeDetails.copy(
            claimantDetails = personalDetailsWithoutIntlAddress
          )
        )
      )

      val section = AdministrativeDetailsSection.build(submissionWithoutIntlAddress)
      section.addressLine1 mustBe "Not Entered"
      section.addressLine2 mustBe "Not Entered"
      section.townOrCity mustBe "Not Entered"
      section.county mustBe Some("Not Entered")
      section.stateOrRegion mustBe Some("Not Entered")
      section.postCode mustBe Some("Not Entered")
      section.postalCode mustBe Some("Not Entered")
      section.country mustBe "United Kingdom"    }

    "should return 'NotEntered' when addressLine2 is not present and is Uk Address" in {
      val ukAddress = UkAddress("Address Line 1", None, "City", Some("County"), "Postcode")
      val personalDetailsWithoutIntlAddress = TestData.administrativeDetails.claimantDetails.copy(
        claimantPersonalDetails = TestData.administrativeDetails.claimantDetails.claimantPersonalDetails.copy(
          address = Some(ukAddress),
          internationalAddress = None
        )
      )
      val submissionWithoutIntlAddress = TestData.finalSubmission.copy(
        submissionInputs = TestData.submissionInputs.copy(
          administrativeDetails = TestData.administrativeDetails.copy(
            claimantDetails = personalDetailsWithoutIntlAddress
          )
        )
      )

      val section = AdministrativeDetailsSection.build(submissionWithoutIntlAddress)
      section.addressLine2 mustBe "Not Entered"
    }

    "should return 'NotEntered' when addressLine2 is not present and is International Address" in {
      val internationalAddress = InternationalAddress("Intl Address Line 1", None, "Intl City", Some("Intl Region"), Some("Postalcode"), "FR")
      val personalDetailsWithoutIntlAddress = TestData.administrativeDetails.claimantDetails.copy(
        claimantPersonalDetails = TestData.administrativeDetails.claimantDetails.claimantPersonalDetails.copy(
          address = None,
          internationalAddress = Some(internationalAddress)
        )
      )
      val submissionWithoutIntlAddress = TestData.finalSubmission.copy(
        submissionInputs = TestData.submissionInputs.copy(
          administrativeDetails = TestData.administrativeDetails.copy(
            claimantDetails = personalDetailsWithoutIntlAddress
          )
        )
      )

      val section = AdministrativeDetailsSection.build(submissionWithoutIntlAddress)
      section.addressLine2 mustBe "Not Entered"
    }
  }
}
