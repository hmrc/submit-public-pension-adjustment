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

import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.{AnnualAllowance, CalculationInputs, ChangeInTaxCharge, ExcessLifetimeAllowancePaid, LifeTimeAllowance, LtaPensionSchemeDetails, LtaProtectionOrEnhancements, Period => InputPeriod, ProtectionType, Resubmission => inputsResubmission, SchemeNameAndTaxRef, TaxYear2016To2023, WhatNewProtectionTypeEnhancement, WhoPaidLTACharge, WhoPayingExtraLtaCharge}
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.response.{CalculationResponse, InDatesTaxYearSchemeCalculation, InDatesTaxYearsCalculation, OutOfDatesTaxYearSchemeCalculation, OutOfDatesTaxYearsCalculation, Period => ResponsePeriod, Resubmission => responseResubmission, TaxYearScheme, TotalAmounts}
import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.OnBehalfOfMemberType.Deceased
import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission._
import uk.gov.hmrc.submitpublicpensionadjustment.models.{PSTR, UkAddress}
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.PDFViewModel
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.sections._

import java.time.LocalDate

object TestData {

  val testTaxYearSchemeData2018 = List(
    TaxYearScheme(
      name = "TestName2018",
      pensionSchemeTaxReference = "TestTaxRef",
      originalPensionInputAmount = 999,
      revisedPensionInputAmount = 991,
      chargePaidByScheme = 100
    ),
    TaxYearScheme(
      name = "TestName22018",
      pensionSchemeTaxReference = "TestTaxRef",
      originalPensionInputAmount = 999,
      revisedPensionInputAmount = 991,
      chargePaidByScheme = 100
    )
  )

  val testTaxYearSchemeData2017 = List(
    TaxYearScheme(
      name = "TestName2017",
      pensionSchemeTaxReference = "TestTaxRef2017",
      originalPensionInputAmount = 999,
      revisedPensionInputAmount = 991,
      chargePaidByScheme = 100
    ),
    TaxYearScheme(
      name = "TestName2222017",
      pensionSchemeTaxReference = "TestTaxRef",
      originalPensionInputAmount = 999,
      revisedPensionInputAmount = 991,
      chargePaidByScheme = 100
    )
  )

  val taxYear2016To2023SampleData2018 = TaxYear2016To2023.NormalTaxYear(
    pensionInputAmount = 5000,
    taxYearSchemes = testTaxYearSchemeData2018,
    totalIncome = 100000,
    chargePaidByMember = 1500,
    period = InputPeriod._2018
  )

  val taxYear2016To2023SampleData2017 = TaxYear2016To2023.NormalTaxYear(
    pensionInputAmount = 5000,
    taxYearSchemes = testTaxYearSchemeData2017,
    totalIncome = 100000,
    chargePaidByMember = 100,
    period = InputPeriod._2017
  )

  val lifeTimeAllowance = LifeTimeAllowance(
    benefitCrystallisationEventFlag = true,
    benefitCrystallisationEventDate = LocalDate.of(2017, 1, 30),
    changeInLifetimeAllowancePercentageInformedFlag = true,
    changeInTaxCharge = ChangeInTaxCharge.NewCharge,
    lifetimeAllowanceProtectionOrEnhancements = LtaProtectionOrEnhancements.Protection,
    protectionType = ProtectionType.PrimaryProtection,
    protectionReference = "originalReference",
    protectionTypeOrEnhancementChangedFlag = true,
    newProtectionTypeOrEnhancement = Some(WhatNewProtectionTypeEnhancement.EnhancedProtection),
    newProtectionTypeOrEnhancementReference = Some("newReference"),
    previousLifetimeAllowanceChargeFlag = true,
    previousLifetimeAllowanceChargePaymentMethod = Some(ExcessLifetimeAllowancePaid.Lumpsum),
    previousLifetimeAllowanceChargeAmount = Some(10000),
    previousLifetimeAllowanceChargePaidBy = Some(WhoPaidLTACharge.PensionScheme),
    previousLifetimeAllowanceChargeSchemeNameAndTaxRef = Some(SchemeNameAndTaxRef("Scheme1", "pstr1")),
    newLifetimeAllowanceChargeAmount = 20000,
    newLifetimeAllowanceChargeWillBePaidBy = Some(WhoPayingExtraLtaCharge.PensionScheme),
    newLifetimeAllowanceChargeSchemeNameAndTaxRef = Some(LtaPensionSchemeDetails("Scheme2", "pstr2"))
  )

  val calculationInputs = CalculationInputs(
    inputsResubmission(false, None),
    Some(
      AnnualAllowance(
        scottishTaxYears = List(),
        taxYears = List(taxYear2016To2023SampleData2018, taxYear2016To2023SampleData2017)
      )
    ),
    None
  )

  val onBehalfOfMemberDetails = OnBehalfOfMember(
    memberPersonalDetails = PersonalDetails(
      fullName = "FirstName Surname",
      alternateName = None,
      dateOfBirth = Some(LocalDate.of(1920, 1, 13)),
      None,
      None,
      pensionSchemeMemberAddress =
        Some(UkAddress("Behalf Address 1", Some("Behalf Address 2"), "City", Some("County"), "Postcode")),
      None,
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
        None,
        None,
        None,
        contactPhoneNumber = Some("1234567890")
      ),
      TaxIdentifiers(Some("someNino"), None, Some("someUtr"))
    ),
    Some(onBehalfOfMemberDetails)
  )

  val declarations = Declarations(
    compensation = true,
    tax = true,
    contactDetails = true,
    powerOfAttorney = Some(false),
    claimOnBehalfOfDeceased = Some(false)
  )

  val individualSchemeIdentifier = IndividualSchemeIdentifier(
    relatedToScheme = SchemeDetails(schemeName = "TestScheme", pstr = PSTR("TestPSTR")),
    legacyReference = Some("legacyReference"),
    reformReference = Some("reformReference")
  )

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

  val bankAccountDetails: Option[BankAccountDetails] = Some(
    BankAccountDetails(
      accountName = "TestAccountName",
      sortCode = "TestSortCode",
      accountNumber = "TestAccountNumber"
    )
  )
  val schemeDetails: SchemeDetails                   = SchemeDetails(schemeName = "TestSceme", pstr = PSTR("schemePstr"))

  val schemeCharge: Option[SchemeCharge] = Some(
    SchemeCharge(amount = 10, schemeDetails = schemeDetails, Some(LocalDate.of(2017, 1, 13)), None)
  )

  val paymentElection: PaymentElection = PaymentElection(period = InputPeriod._2017, None, schemeCharge)

  val submissionInputs: SubmissionInputs =
    SubmissionInputs(
      administrativeDetails,
      List(paymentElection),
      List(individualSchemeIdentifier),
      schemeTaxRelief,
      bankAccountDetails,
      declarations
    )

  val outOfDatesCalculation2017 = OutOfDatesTaxYearsCalculation(
    period = ResponsePeriod._2017,
    directCompensation = 100,
    indirectCompensation = 200,
    chargePaidByMember = 50,
    chargePaidBySchemes = 75,
    revisedChargableAmountBeforeTaxRate = 300,
    revisedChargableAmountAfterTaxRate = 270,
    unusedAnnualAllowance = 20,
    taxYearSchemes = List(OutOfDatesTaxYearSchemeCalculation("Scheme A", "PSTR123", 50))
  )

  val outOfDatesCalculation2018 = OutOfDatesTaxYearsCalculation(
    period = ResponsePeriod.Year(2018),
    directCompensation = 1002018,
    indirectCompensation = 2002018,
    chargePaidByMember = 502018,
    chargePaidBySchemes = 752018,
    revisedChargableAmountBeforeTaxRate = 3002018,
    revisedChargableAmountAfterTaxRate = 2702018,
    unusedAnnualAllowance = 202018,
    taxYearSchemes = List(OutOfDatesTaxYearSchemeCalculation("Scheme A2018", "PSTR1232018", 502018))
  )

  val inDatesCalculation2017 = InDatesTaxYearsCalculation(
    period = ResponsePeriod.Year(2017),
    memberCredit = 50,
    schemeCredit = 150,
    debit = 25,
    chargePaidByMember = 50,
    chargePaidBySchemes = 100,
    revisedChargableAmountBeforeTaxRate = 300,
    revisedChargableAmountAfterTaxRate = 270,
    unusedAnnualAllowance = 20,
    taxYearSchemes = List(InDatesTaxYearSchemeCalculation("Scheme B", "PSTR456", 100))
  )

  val inDatesCalculation2018 = InDatesTaxYearsCalculation(
    period = ResponsePeriod.Year(2018),
    memberCredit = 3526,
    schemeCredit = 1141550,
    debit = 636,
    chargePaidByMember = 0,
    chargePaidBySchemes = 100,
    revisedChargableAmountBeforeTaxRate = 4453,
    revisedChargableAmountAfterTaxRate = 3414,
    unusedAnnualAllowance = 151525,
    taxYearSchemes = List(InDatesTaxYearSchemeCalculation("Scheme B2018", "PSTR4562018", 100))
  )

  val calculationResponse: Some[CalculationResponse] =
    Some(
      CalculationResponse(
        responseResubmission(false, None),
        TotalAmounts(10, 20, 30),
        List(outOfDatesCalculation2017, outOfDatesCalculation2018),
        List(inDatesCalculation2017, inDatesCalculation2018)
      )
    )

  val finalSubmission = FinalSubmission(calculationInputs, calculationResponse, submissionInputs)

  val administrativeDetailsSection = AdministrativeDetailsSection(
    firstName = "firstName",
    surname = "surname",
    dob = "dob",
    addressLine1 = "addressLine1",
    addressLine2 = "addressLine2",
    postCode = Some("postCode"),
    country = "country",
    utr = "utr",
    townOrCity = "town",
    county = Some("county"),
    postalCode = Some("postalCode"),
    stateOrRegion = Some("state"),
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
    CaseIdentificationSection("compRef", "CompensationAmendment", "miniRegimeRef", "Adjustment", "ltaRef"),
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
    DeclarationsSection("Y", "Y", "Y", "Y", "Y")
  )
}
