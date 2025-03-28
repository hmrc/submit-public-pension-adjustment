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

package uk.gov.hmrc.submitpublicpensionadjustment

import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.{AnnualAllowance, AnnualAllowanceSetup, CalculationInputs, EnhancementType, ExcessLifetimeAllowancePaid, IncomeSubJourney, LifeTimeAllowance, LifetimeAllowanceSetup, LtaPensionSchemeDetails, LtaProtectionOrEnhancements, MaybePIAIncrease, MaybePIAUnchangedOrDecreased, NewEnhancementType, NewExcessLifetimeAllowancePaid, NewLifeTimeAllowanceAdditions, Period as InputPeriod, ProtectionEnhancedChanged, ProtectionType, QuarterChargePaid, Resubmission as inputsResubmission, SchemeNameAndTaxRef, Setup, TaxYear2016To2023, UserSchemeDetails, WhatNewProtectionTypeEnhancement, WhoPaidLTACharge, WhoPayingExtraLtaCharge, YearChargePaid}
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.response.{CalculationResponse, InDatesTaxYearSchemeCalculation, InDatesTaxYearsCalculation, OutOfDatesTaxYearSchemeCalculation, OutOfDatesTaxYearsCalculation, Period as ResponsePeriod, Resubmission as responseResubmission, TaxYearScheme, TotalAmounts}
import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.*
import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.OnBehalfOfMemberType.Deceased
import uk.gov.hmrc.submitpublicpensionadjustment.models.{PSTR, UkAddress}
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.PDFViewModel
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.sections.*

import java.time.LocalDate

object TestData {

  val testTaxYearSchemeData2019 = List(
    TaxYearScheme(
      name = "TestName2019",
      pensionSchemeTaxReference = "TestTaxRef",
      revisedPensionInputAmount = 991,
      chargePaidByScheme = 100,
      None
    ),
    TaxYearScheme(
      name = "TestName22019",
      pensionSchemeTaxReference = "TestTaxRef",
      revisedPensionInputAmount = 991,
      chargePaidByScheme = 100,
      None
    )
  )

  val testTaxYearSchemeData2018 = List(
    TaxYearScheme(
      name = "TestName2018",
      pensionSchemeTaxReference = "TestTaxRef",
      revisedPensionInputAmount = 991,
      chargePaidByScheme = 100,
      None
    ),
    TaxYearScheme(
      name = "TestName22018",
      pensionSchemeTaxReference = "TestTaxRef",
      revisedPensionInputAmount = 991,
      chargePaidByScheme = 100,
      None
    )
  )

  val testTaxYearSchemeData2017 = List(
    TaxYearScheme(
      name = "TestName2017",
      pensionSchemeTaxReference = "TestTaxRef2017",
      revisedPensionInputAmount = 991,
      chargePaidByScheme = 100,
      None
    ),
    TaxYearScheme(
      name = "TestName2222017",
      pensionSchemeTaxReference = "TestTaxRef",
      revisedPensionInputAmount = 991,
      chargePaidByScheme = 100,
      None
    )
  )

  val incomeSubJourney = IncomeSubJourney(
    Some(1500),
    Some(150),
    Some(100),
    Some(100),
    Some(false),
    Some(300),
    Some(450),
    Some(200),
    Some(125),
    Some(10),
    Some(10),
    Some(25),
    Some(300),
    Some(125),
    Some(50000),
    Some(100)
  )

  val taxYear2016To2023SampleData2019 = TaxYear2016To2023.PostFlexiblyAccessedTaxYear(
    definedBenefitInputAmount = 5000,
    definedContributionInputAmount = 300,
    taxYearSchemes = testTaxYearSchemeData2019,
    totalIncome = 100000,
    chargePaidByMember = 1500,
    period = InputPeriod._2019,
    incomeSubJourney = incomeSubJourney
  )

  val taxYear2016To2023SampleData2018 = TaxYear2016To2023.InitialFlexiblyAccessedTaxYear(
    definedBenefitInputAmount = 5000,
    flexiAccessDate = LocalDate.of(2016, 4, 6),
    preAccessDefinedContributionInputAmount = 300,
    postAccessDefinedContributionInputAmount = 200,
    taxYearSchemes = testTaxYearSchemeData2018,
    totalIncome = 100000,
    chargePaidByMember = 1500,
    period = InputPeriod._2018,
    incomeSubJourney = incomeSubJourney
  )

  val taxYear2016To2023SampleData2017 = TaxYear2016To2023.NormalTaxYear(
    pensionInputAmount = 5000,
    taxYearSchemes = testTaxYearSchemeData2017,
    totalIncome = 100000,
    chargePaidByMember = 100,
    period = InputPeriod._2017,
    incomeSubJourney = incomeSubJourney
  )

  val newLifeTimeAllowanceAdditions = NewLifeTimeAllowanceAdditions(
    enhancementType = Some(EnhancementType.Both),
    internationalEnhancementReference = Some("internationalRef"),
    pensionCreditReference = Some("pensionCredRef"),
    newEnhancementType = Some(NewEnhancementType.Both),
    newInternationalEnhancementReference = Some("newinternationEnhancementRef"),
    newPensionCreditReference = Some("newPensionCredRef"),
    lumpSumValue = Some(5),
    annualPaymentValue = Some(6),
    userSchemeDetails = Some(UserSchemeDetails("name", "ref")),
    quarterChargePaid = Some(QuarterChargePaid.AprToJul),
    yearChargePaid = Some(YearChargePaid._2015To2016),
    newExcessLifetimeAllowancePaid = Some(NewExcessLifetimeAllowancePaid.Both),
    newLumpSumValue = Some(7),
    newAnnualPaymentValue = Some(8)
  )

  val lifeTimeAllowance = LifeTimeAllowance(
    benefitCrystallisationEventDate = LocalDate.of(2017, 1, 30),
    lifetimeAllowanceProtectionOrEnhancements = LtaProtectionOrEnhancements.Protection,
    protectionType = Some(ProtectionType.PrimaryProtection),
    protectionReference = Some("originalReference"),
    protectionTypeEnhancementChanged = ProtectionEnhancedChanged.Protection,
    newProtectionTypeOrEnhancement = Some(WhatNewProtectionTypeEnhancement.EnhancedProtection),
    newProtectionTypeOrEnhancementReference = Some("newReference"),
    previousLifetimeAllowanceChargeFlag = true,
    previousLifetimeAllowanceChargePaymentMethod = Some(ExcessLifetimeAllowancePaid.Lumpsum),
    previousLifetimeAllowanceChargePaidBy = Some(WhoPaidLTACharge.PensionScheme),
    previousLifetimeAllowanceChargeSchemeNameAndTaxRef = Some(SchemeNameAndTaxRef("Scheme1", "pstr1")),
    newLifetimeAllowanceChargeWillBePaidBy = Some(WhoPayingExtraLtaCharge.PensionScheme),
    newLifetimeAllowanceChargeSchemeNameAndTaxRef = Some(LtaPensionSchemeDetails("Scheme2", "pstr2")),
    newLifeTimeAllowanceAdditions = newLifeTimeAllowanceAdditions
  )

  val annualAllowance: AnnualAllowance = AnnualAllowance(
    scottishTaxYears = List(),
    taxYears = List(taxYear2016To2023SampleData2019, taxYear2016To2023SampleData2018, taxYear2016To2023SampleData2017)
  )

  val calculationInputs = CalculationInputs(
    inputsResubmission(false, None),
    Setup(
      Some(
        AnnualAllowanceSetup(
          Some(true),
          Some(false),
          Some(false),
          Some(false),
          Some(false),
          Some(false),
          Some(MaybePIAIncrease.No),
          Some(MaybePIAUnchangedOrDecreased.No),
          Some(false),
          Some(false),
          Some(false),
          Some(false)
        )
      ),
      Some(
        LifetimeAllowanceSetup(
          Some(true),
          Some(false),
          Some(true),
          Some(false),
          Some(false),
          Some(false),
          Some(true)
        )
      )
    ),
    Some(
      annualAllowance
    ),
    Some(lifeTimeAllowance)
  )

  val calculationInputsWithResubmissionReason = CalculationInputs(
    inputsResubmission(true, Some("Test resubmission reason")),
    Setup(
      Some(
        AnnualAllowanceSetup(
          Some(true),
          Some(false),
          Some(false),
          Some(false),
          Some(false),
          Some(false),
          Some(MaybePIAIncrease.No),
          Some(MaybePIAUnchangedOrDecreased.No),
          Some(false),
          Some(false),
          Some(false),
          Some(false)
        )
      ),
      Some(
        LifetimeAllowanceSetup(
          Some(true),
          Some(false),
          Some(true),
          Some(false),
          Some(false),
          Some(false),
          Some(true)
        )
      )
    ),
    Some(
      annualAllowance
    ),
    Some(lifeTimeAllowance)
  )

  val onBehalfOfMemberDetails = OnBehalfOfMember(
    memberPersonalDetails = PersonalDetails(
      fullName = "FirstName Surname",
      alternateName = None,
      dateOfBirth = Some(LocalDate.of(1920, 1, 13)),
      None,
      None,
      pensionSchemeMemberAddress = Some(
        UkAddress(
          Some("org"),
          "Behalf Address 1",
          Some("Behalf Address 2"),
          Some("Behalf Address 3"),
          "City",
          Some("County"),
          Some("Postcode"),
          Some("United Kingdom")
        )
      ),
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
            Some("org"),
            "testLine1",
            Some("testLine2"),
            Some("testLine3"),
            "TestCity",
            Some("TestCounty"),
            Some("Postcode"),
            None
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
    claimOnBehalfOfDeceased = Some(false),
    legalPersonalRepresentative = Some(false),
    schemeCreditConsent = Some(true)
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
      accountNumber = "TestAccountNumber",
      rollNumber = Some("TestRollNumber")
    )
  )
  val schemeDetails: SchemeDetails                   = SchemeDetails(schemeName = "TestSceme", pstr = PSTR("schemePstr"))

  val schemeCharge: Option[SchemeCharge] = Some(
    SchemeCharge(amount = 10, schemeDetails = schemeDetails, Some(LocalDate.of(2017, 1, 13)))
  )

  val schemeChargeWithEstimatedElectionQuater: Option[SchemeCharge] = Some(
    SchemeCharge(amount = 10, schemeDetails = schemeDetails, None)
  )

  val paymentElection: PaymentElection = PaymentElection(period = InputPeriod._2017, None, schemeCharge)

  val paymentElection2018: PaymentElection =
    PaymentElection(period = InputPeriod._2018, None, schemeChargeWithEstimatedElectionQuater)

  val submissionInputs: SubmissionInputs =
    SubmissionInputs(
      administrativeDetails,
      List(paymentElection, paymentElection2018),
      List(individualSchemeIdentifier),
      schemeTaxRelief,
      bankAccountDetails,
      declarations
    )

  val outOfDatesCalculation2019 = OutOfDatesTaxYearsCalculation(
    period = ResponsePeriod.Year(2019),
    directCompensation = 1002019,
    indirectCompensation = 2002019,
    chargePaidByMember = 502019,
    chargePaidBySchemes = 752019,
    revisedChargableAmountBeforeTaxRate = 3002019,
    revisedChargableAmountAfterTaxRate = 2702019,
    unusedAnnualAllowance = 202019,
    taxYearSchemes = List(OutOfDatesTaxYearSchemeCalculation("Scheme A2019", "PSTR1232019", 502018)),
    adjustedCompensation = Some(1747981)
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
    taxYearSchemes = List(OutOfDatesTaxYearSchemeCalculation("Scheme A", "PSTR123", 50)),
    adjustedCompensation = Some(175)
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
    taxYearSchemes = List(OutOfDatesTaxYearSchemeCalculation("Scheme A2018", "PSTR1232018", 502018)),
    adjustedCompensation = Some(1747982)
  )

  val inDatesCalculation2019: InDatesTaxYearsCalculation = InDatesTaxYearsCalculation(
    period = ResponsePeriod.Year(2019),
    memberCredit = 50,
    schemeCredit = 150,
    debit = 25,
    chargePaidByMember = 50,
    chargePaidBySchemes = 0,
    revisedChargableAmountBeforeTaxRate = 300,
    revisedChargableAmountAfterTaxRate = 270,
    unusedAnnualAllowance = 20,
    taxYearSchemes = List(InDatesTaxYearSchemeCalculation("Scheme B2019", "PSTR4562019", 100)),
    totalCompensation = Some(-250)
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
    taxYearSchemes = List(InDatesTaxYearSchemeCalculation("Scheme B", "PSTR456", 100)),
    totalCompensation = Some(-150)
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
    taxYearSchemes = List(InDatesTaxYearSchemeCalculation("Scheme B2018", "PSTR4562018", 100)),
    totalCompensation = Some(-4353)
  )

  val calculationResponse: CalculationResponse = CalculationResponse(
    responseResubmission(false, None),
    TotalAmounts(10, 20, 30),
    List(outOfDatesCalculation2017, outOfDatesCalculation2018, outOfDatesCalculation2019),
    List(inDatesCalculation2017, inDatesCalculation2018, inDatesCalculation2019)
  )

  val finalSubmission = FinalSubmission(calculationInputs, Some(calculationResponse), submissionInputs)

  val finalSubmissionWithResubmissionReason =
    FinalSubmission(calculationInputsWithResubmissionReason, Some(calculationResponse), submissionInputs)

  val incomeSubJourneySubJourneySection = IncomeSubJourneySection(
    periodRange = "2017/18",
    incomeSubJourneySubSection = IncomeSubJourneySubSection(
      "1",
      "1",
      "1",
      "1"
    )
  )

  val administrativeDetailsSection = AdministrativeDetailsSection(
    firstName = "firstName",
    surname = "surname",
    dob = "dob",
    organisation = Some("org"),
    addressLine1 = "addressLine1",
    addressLine2 = "addressLine2",
    addressLine3 = "addressLine3",
    postCode = Some("postCode"),
    country = "country",
    utr = "utr",
    townOrCity = "town",
    county = Some("county"),
    postalCode = Some("postalCode"),
    stateOrRegion = Some("state"),
    ninoOrTrn = "ninoOrTrn",
    contactNumber = "contactNumber",
    resubmissionReason = Some("resubmissionReason")
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
    Seq(incomeSubJourneySubJourneySection),
    Seq(),
    None,
    Some(
      PaymentInformationSection(
        accountName = "accountName",
        sortCode = "sortCode",
        accountNumber = "accountNumber",
        rollNumber = Some("rollNumber")
      )
    ),
    DeclarationsSection("Y", "Y", "Y", "Y", "Y", "Y", "Consent given")
  )
}
