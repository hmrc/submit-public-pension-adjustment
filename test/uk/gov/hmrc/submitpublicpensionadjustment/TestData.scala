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

package uk.gov.hmrc.submitpublicpensionadjustment

import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.{CalculationInputs, LifeTimeAllowance, Resubmission}
import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission._
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.PDFViewModel
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.sections.{AdministrativeDetailsSection, CaseIdentificationSection, DeclarationsSection, PaymentInformationSection}

object TestData {

  val calculationInputs = CalculationInputs(Resubmission(false, None), None, Some(LifeTimeAllowance("placeholder")))

  val administrativeDetails = AdministrativeDetails(
    ClaimantDetails(
      PersonalDetails(
        fullName = "FirstName Surname",
        alternateName = None,
        dateOfBirth = None,
        address = None,
        internationalAddress = None,
        contactPhoneNumber = None
      ),
      TaxIdentifiers(Some("someNino"), None, None)
    ),
    None
  )

  val declarations = Declarations(
    compensation = true,
    tax = true,
    contactDetails = true,
    powerOfAttorney = None,
    claimOnBehalfOfDeceased = None
  )

  val submissionInputs: SubmissionInputs =
    SubmissionInputs(administrativeDetails, List.empty, List.empty, None, None, declarations)

  val finalSubmission = FinalSubmission(calculationInputs, None, submissionInputs)

  val administrativeDetailsSection = AdministrativeDetailsSection(
    firstName = "firstName",
    surname = "surname",
    dob = "dob",
    addressLine1 = "addressLine1",
    addressLine2 = "addressLine2",
    postCode = "postCode",
    country = Some("country"),
    utr = Some("utr"),
    ninoOrTrn = "ninoOrTrn",
    contactNumber = "contactNumber"
  )

  val viewModel = PDFViewModel(
    "caseNumber",
    CaseIdentificationSection(Some("compRef"), None, Some("miniRegimeRef"), None, Some("ltaRef")),
    administrativeDetailsSection,
    None,
    None,
    Seq(),
    Seq(),
    Seq(),
    None,
    Some(
      PaymentInformationSection(accountName = "accountName", sortCode = "sortCode", accountNumber = "accountNumber")
    ),
    DeclarationsSection("Y", "Y", "Y", "Y", "Y", "Y", "Y")
  )
}
