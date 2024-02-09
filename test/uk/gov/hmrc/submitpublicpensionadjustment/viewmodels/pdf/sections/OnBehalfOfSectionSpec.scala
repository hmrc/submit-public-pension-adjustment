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
import uk.gov.hmrc.submitpublicpensionadjustment.TestData.onBehalfOfMemberDetails
import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.TaxIdentifiers
import uk.gov.hmrc.submitpublicpensionadjustment.models.{InternationalAddress, UkAddress}

import java.time.LocalDate

class OnBehalfOfSectionSpec extends AnyFreeSpec with Matchers with Logging {

  "section must be constructed based on final submission" in {

    val section = OnBehalfOfSection.build(TestData.finalSubmission)

    section mustBe Some(
      OnBehalfOfSection(
        firstName = "FirstName",
        surname = "Surname",
        dob = "13/01/1920",
        addressLine1 = "Behalf Address 1",
        addressLine2 = "Behalf Address 2",
        townOrCity = "City",
        county = Some("County"),
        stateOrRegion = None,
        postCode = Some("Postcode"),
        postalCode = None,
        country = "United Kingdom",
        utr = "someUTR",
        ninoOrTrn = "someNino"
      )
    )
  }

  "when constructing 'dob'" - {
    "should return formatted date if dateOfBirth is present" in {

      val finalSubmissionWithDOB = TestData.finalSubmission.copy(
        submissionInputs = TestData.submissionInputs.copy(
          administrativeDetails = TestData.administrativeDetails.copy(
            TestData.administrativeDetails.claimantDetails,
            Some(
              TestData.onBehalfOfMemberDetails.copy(
                memberPersonalDetails = TestData.onBehalfOfMemberDetails.memberPersonalDetails.copy(
                  dateOfBirth = Some(LocalDate.of(1980, 1, 1))
                )
              )
            )
          )
        )
      )
      val section                = OnBehalfOfSection.build(finalSubmissionWithDOB)
      section.get.dob mustBe "01/01/1980"
    }

    "should return 'Not Entered' if dateOfBirth is None" in {
      val finalSubmissionWithoutDOB = TestData.finalSubmission.copy(
        submissionInputs = TestData.submissionInputs.copy(
          administrativeDetails = TestData.administrativeDetails.copy(
            TestData.administrativeDetails.claimantDetails,
            Some(
              TestData.onBehalfOfMemberDetails.copy(
                memberPersonalDetails = TestData.onBehalfOfMemberDetails.memberPersonalDetails.copy(
                  dateOfBirth = None
                )
              )
            )
          )
        )
      )
      val section                   = OnBehalfOfSection.build(finalSubmissionWithoutDOB)
      section.get.dob mustBe "Not Entered"
    }

    "when constructing address lines" - {
      "should use international address when present" in {
        val internationalAddress                   = InternationalAddress(
          "Intl Address Line 1",
          Some("Intl Address Line 2"),
          "Intl City",
          Some("Intl Region"),
          Some("Postalcode"),
          "FR"
        )
        val onBehalfOfMemberDetailsWithIntlAddress = TestData.onBehalfOfMemberDetails.copy(
          memberPersonalDetails = TestData.onBehalfOfMemberDetails.memberPersonalDetails.copy(
            pensionSchemeMemberAddress = None,
            pensionSchemeMemberInternationalAddress = Some(internationalAddress)
          )
        )
        val finalSubmissionWithIntlAddress         = TestData.finalSubmission.copy(
          submissionInputs = TestData.submissionInputs.copy(
            administrativeDetails = TestData.administrativeDetails
              .copy(TestData.administrativeDetails.claimantDetails, Some(onBehalfOfMemberDetailsWithIntlAddress))
          )
        )

        val section = OnBehalfOfSection.build(finalSubmissionWithIntlAddress)
        section.get.addressLine1 mustBe "Intl Address Line 1"
        section.get.addressLine2 mustBe "Intl Address Line 2"
        section.get.townOrCity mustBe "Intl City"
        section.get.county mustBe None
        section.get.stateOrRegion mustBe Some("Intl Region")
        section.get.postCode mustBe None
        section.get.postalCode mustBe Some("Postalcode")
        section.get.country mustBe "FR"
      }

      "should return 'NotEntered' when both address and internationalAddress are not present" in {
        val onBehalfOfMemberDetailsWithIntlAddress = TestData.onBehalfOfMemberDetails.copy(
          memberPersonalDetails = TestData.onBehalfOfMemberDetails.memberPersonalDetails.copy(
            pensionSchemeMemberAddress = None,
            pensionSchemeMemberInternationalAddress = None
          )
        )
        val finalSubmissionWithIntlAddress         = TestData.finalSubmission.copy(
          submissionInputs = TestData.submissionInputs.copy(
            administrativeDetails = TestData.administrativeDetails
              .copy(TestData.administrativeDetails.claimantDetails, Some(onBehalfOfMemberDetailsWithIntlAddress))
          )
        )

        val section = OnBehalfOfSection.build(finalSubmissionWithIntlAddress)
        section.get.addressLine1 mustBe "Not Entered"
        section.get.addressLine2 mustBe "Not Entered"
        section.get.townOrCity mustBe "Not Entered"
        section.get.county mustBe Some("Not Entered")
        section.get.stateOrRegion mustBe Some("Not Entered")
        section.get.postCode mustBe Some("Not Entered")
        section.get.postalCode mustBe Some("Not Entered")
        section.get.country mustBe "United Kingdom"
      }

      "should return 'NotEntered' when addressLine2 is not present and is international Address" in {
        val internationalAddress                   =
          InternationalAddress("Intl Address Line 1", None, "Intl City", Some("Intl Region"), Some("Postalcode"), "FR")
        val onBehalfOfMemberDetailsWithIntlAddress = TestData.onBehalfOfMemberDetails.copy(
          memberPersonalDetails = TestData.onBehalfOfMemberDetails.memberPersonalDetails.copy(
            pensionSchemeMemberAddress = None,
            pensionSchemeMemberInternationalAddress = Some(internationalAddress)
          )
        )
        val finalSubmissionWithIntlAddress         = TestData.finalSubmission.copy(
          submissionInputs = TestData.submissionInputs.copy(
            administrativeDetails = TestData.administrativeDetails
              .copy(TestData.administrativeDetails.claimantDetails, Some(onBehalfOfMemberDetailsWithIntlAddress))
          )
        )

        val section = OnBehalfOfSection.build(finalSubmissionWithIntlAddress)
        section.get.addressLine2 mustBe "Not Entered"
      }

      "should return 'NotEntered' when addressLine2 is not present and is Uk Address" in {
        val ukAddress                              = UkAddress("Address Line 1", None, "City", Some("County"), "Postcode")
        val onBehalfOfMemberDetailsWithIntlAddress = TestData.onBehalfOfMemberDetails.copy(
          memberPersonalDetails = TestData.onBehalfOfMemberDetails.memberPersonalDetails.copy(
            pensionSchemeMemberAddress = Some(ukAddress),
            pensionSchemeMemberInternationalAddress = None
          )
        )
        val finalSubmissionWithIntlAddress         = TestData.finalSubmission.copy(
          submissionInputs = TestData.submissionInputs.copy(
            administrativeDetails = TestData.administrativeDetails
              .copy(TestData.administrativeDetails.claimantDetails, Some(onBehalfOfMemberDetailsWithIntlAddress))
          )
        )

        val section = OnBehalfOfSection.build(finalSubmissionWithIntlAddress)
        section.get.addressLine2 mustBe "Not Entered"
      }
    }

    "when constructing UTR" - {
      "should return trn correctly" in {
        val finalSubmissionWithUtr = TestData.finalSubmission.copy(
          submissionInputs = TestData.submissionInputs.copy(
            administrativeDetails = TestData.administrativeDetails.copy(
              TestData.administrativeDetails.claimantDetails,
              Some(
                TestData.onBehalfOfMemberDetails.copy(
                  taxIdentifiers = TaxIdentifiers(None, Some("testTrn"), None),
                  memberPersonalDetails = TestData.onBehalfOfMemberDetails.memberPersonalDetails
                )
              )
            )
          )
        )
        val sectionWithUTR         = OnBehalfOfSection.build(finalSubmissionWithUtr)
        sectionWithUTR.get.ninoOrTrn mustBe "testTrn"
      }
    }
  }
}
