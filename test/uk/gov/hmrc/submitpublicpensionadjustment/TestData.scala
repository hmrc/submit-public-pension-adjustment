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

import uk.gov.hmrc.submitpublicpensionadjustment.models.{PSTR, UkAddress}
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.{CalculationInputs, LifeTimeAllowance, Resubmission => inputsResubmission}
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.response.{CalculationResponse, InDatesTaxYearSchemeCalculation, InDatesTaxYearsCalculation, OutOfDatesTaxYearSchemeCalculation, OutOfDatesTaxYearsCalculation, Period, Resubmission => responseResubmission, TotalAmounts}
import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.OnBehalfOfMemberType.Deceased
import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission._
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.PDFViewModel
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.sections.{AdditionalOrHigherReliefSection, AdministrativeDetailsSection, CaseIdentificationSection, DeclarationsSection, PaymentInformationSection}

import java.time.LocalDate

object TestData {

  val calculationInputs = CalculationInputs(inputsResubmission(false, None), None, Some(LifeTimeAllowance("test")))

  // OnBehalfOfSection

  val onBehalfOfMemberDetails = OnBehalfOfMember(
    memberPersonalDetails = PersonalDetails(
      fullName = "FirstName Surname",
      alternateName = None,
      dateOfBirth = Some(LocalDate.of(1920, 1, 13)),
      address = Some(UkAddress("Behalf Address 1", Some("Behalf Address 2"), "City", Some("County"), "Postcode")),
      internationalAddress = None,
      contactPhoneNumber = Some("1234567890")
    ),
    taxIdentifiers = TaxIdentifiers(Some("someNino"), None, Some("someUTR")),
    dateOfDeath = Some(LocalDate.of(2017, 1, 13)),
    memberType = Deceased
  )

  val administrativeDetails = AdministrativeDetails(
    ClaimantDetails(
      PersonalDetails(
        fullName = "FirstName Surname",
        alternateName = None,
        dateOfBirth = Some(LocalDate.of(1920, 1, 13)),
        address = Some(
          UkAddress(
            "testLine1",
            Some("testLine2"),
            "TestCity",
            Some("TestCounty"),
            "Postcode"
          )
        ),
        internationalAddress = None,
        contactPhoneNumber = None
      ),
      TaxIdentifiers(Some("someNino"), None, None)
    ),
    Some(onBehalfOfMemberDetails)
  )
  // DeclarationsSection
  val declarations          = Declarations(
    compensation = true,
    tax = true,
    contactDetails = true,
    powerOfAttorney = Some(false),
    claimOnBehalfOfDeceased = Some(false)
  )

  // PublicSectorSchemeDetailsSection

  val individualSchemeIdentifier = IndividualSchemeIdentifier(
    relatedToScheme = SchemeDetails(schemeName = "TestScheme", pstr = PSTR("TestPSTR")),
    legacyReference = None,
    reformReference = Some("reformReference")
  )

  // AditionalOrHigherRefliefSection

  val schemeTaxRelief = Some(
    SchemeTaxRelief(
      amount = 1000,
      individualSchemeIdentifier = IndividualSchemeIdentifier(
        relatedToScheme = SchemeDetails(schemeName = "SchemeA", pstr = PSTR("schemePstr")),
        legacyReference = None,
        reformReference = None
      )
    )
  )

  // PaymentInformationSection

  val bankAccountDetails: Option[BankAccountDetails] = Some(
    BankAccountDetails(
      accountName = "TestAccountName",
      sortCode = "TestSortCode",
      accountNumber = "TestAccountNumber"
    )
  )

  val submissionInputs: SubmissionInputs =
    SubmissionInputs(
      administrativeDetails,
      List.empty,
      List(individualSchemeIdentifier),
      schemeTaxRelief,
      bankAccountDetails,
      declarations
    )

  // CompensationSection
  val outOfDatesCalculation = OutOfDatesTaxYearsCalculation(
    period = Period.Year(2017),
    directCompensation = 100,
    indirectCompensation = 200,
    chargePaidByMember = 50,
    chargePaidBySchemes = 75,
    revisedChargableAmountBeforeTaxRate = 300,
    revisedChargableAmountAfterTaxRate = 270,
    unusedAnnualAllowance = 20,
    taxYearSchemes = List(OutOfDatesTaxYearSchemeCalculation("Scheme A", "PSTR123", 50))
  )

  val inDatesCalculation = InDatesTaxYearsCalculation(
    period = Period.Year(2017),
    memberCredit = 50,
    schemeCredit = 150,
    debit = 25,
    chargePaidByMember = 50,
    chargePaidBySchemes = 75,
    revisedChargableAmountBeforeTaxRate = 300,
    revisedChargableAmountAfterTaxRate = 270,
    unusedAnnualAllowance = 20,
    taxYearSchemes = List(InDatesTaxYearSchemeCalculation("Scheme B", "PSTR456", 100))
  )

  val calculationResponse: Some[CalculationResponse] =
    Some(
      CalculationResponse(
        responseResubmission(false, None),
        TotalAmounts(10, 20, 30),
        List(outOfDatesCalculation),
        List(inDatesCalculation)
      )
    )

  val finalSubmission = FinalSubmission(calculationInputs, calculationResponse, submissionInputs)

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

  val additionalOrHigherReliefSection = AdditionalOrHigherReliefSection(
    amount = "1000",
    schemePayingName = "SchemeA",
    schemePayingPstr = "schemePstr"
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
