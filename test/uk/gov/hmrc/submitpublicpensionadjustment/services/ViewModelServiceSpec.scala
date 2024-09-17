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

package uk.gov.hmrc.submitpublicpensionadjustment.services

import org.mockito.MockitoSugar
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import uk.gov.hmrc.submitpublicpensionadjustment.TestData
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.{CalculationInputs, LifetimeAllowanceSetup, Setup}
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.response.Period
import uk.gov.hmrc.submitpublicpensionadjustment.models.dms.{Compensation, MiniRegime}
import uk.gov.hmrc.submitpublicpensionadjustment.models.{CaseIdentifiers, QueueReference}
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.PDFViewModel
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.sections._

class ViewModelServiceSpec extends AnyFreeSpec with Matchers with MockitoSugar {

  private val caseNumber      = "testCaseNumber"
  private val queueReferences =
    Seq(
      QueueReference(Compensation("Compensation_Queue"), "submissionReference1"),
      QueueReference(MiniRegime("MiniRegime_Queue"), "submissionReference2")
    )

  private val finalSubmission = TestData.finalSubmission
  private val caseIdentifiers = CaseIdentifiers(caseNumber, queueReferences)

  private val viewModelService = new ViewModelService()

  private val caseIdentificationSection = CaseIdentificationSection(
    compensation = "submissionReference1",
    compensationAmendment = "Not Applicable",
    miniRegime = "submissionReference2",
    miniRegimeAmendment = "Not Applicable",
    lta = "Not Applicable"
  )

  private val administrativeDetailsSection = AdministrativeDetailsSection(
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

  private val onBehalfOfSection = OnBehalfOfSection(
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

  private val ltaSection = LifetimeAllowanceSection(
    hadBce = "Yes",
    bceDate = "30/01/2017",
    changeInLtaPercentage = "Yes",
    increaseInLTACharge = "No",
    newLTACharge = "No",
    multipleBenefitCrystallisationEvent = "No",
    haveLtaProtectionOrEnhancement = "Protection",
    protectionType = "Primary protection",
    protectionReference = "originalReference",
    enhancementType = "Both",
    internationalEnhancementReference = "internationalRef",
    pensionCreditReference = "pensionCredRef",
    changeToProtectionType = "Protection",
    newProtectionTypeOrEnhancement = "Enhanced protection",
    newProtectionTypeOrReference = "newReference",
    newEnhancementType = "Both",
    newInternationalEnhancementReference = "newinternationEnhancementRef",
    newPensionCreditReference = "newPensionCredRef",
    hadLtaCharge = "Yes",
    howExcessPaid = "Lump Sum",
    lumpSumValue = "£5",
    annualPaymentValue = "£6",
    whoPaidLtaCharge = "Scheme",
    schemeThatPaidChargeName = "Scheme1",
    schemeThatPaidChargeTaxRef = "pstr1",
    yearChargePaid = "6 April 2015 to 5 April 2016",
    quarterChargePaid = "1 April to 30 June",
    newExcessLifetimeAllowancePaid = "Both",
    newLumpSumValue = "£7",
    newAnnualPaymentValue = "£8",
    whoPayingExtraCharge = "Scheme",
    whoPayingExtraChargeSchemeName = "Scheme2",
    whoPayingExtraChargeTaxRef = "pstr2"
  )

  private val publicSectorSchemeDetailsSections = Seq(
    PublicSectorSchemeDetailsSection(
      schemeName = "TestScheme",
      pstr = "TestPSTR",
      reformReference = "reformReference",
      legacyReference = "legacyReference"
    )
  )

  private val compensationSections = Seq(
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
    ),
    CompensationSection(
      Period.Year(2019),
      "£1002019",
      "£2002019",
      "£2702019",
      "£502019",
      Seq(
        SchemePaidChargeDetailsSubSection(1, 100, "TestName2019", "TestTaxRef"),
        SchemePaidChargeDetailsSubSection(2, 100, "TestName22019", "TestTaxRef")
      )
    )
  )

  private val taxAdministrationFrameworkSections = Seq(
    TaxAdministrationFrameworkSection(
      relatingTo = Period._2017,
      previousChargeAmount = "£300",
      whoChargePaidBy = "Both",
      creditValue = "£200",
      debitValue = "£25",
      isSchemePayingCharge = "Yes",
      schemePaymentElectionDate = "13/01/2017",
      schemePayingChargeAmount = "10",
      schemePayingPstr = "schemePstr",
      schemePayingName = "TestSceme",
      schemeDetailsSubSections = Seq(
        SchemeDetailsSubSection(1, "TestName2017", "TestTaxRef2017"),
        SchemeDetailsSubSection(2, "TestName2222017", "TestTaxRef")
      )
    ),
    TaxAdministrationFrameworkSection(
      relatingTo = Period._2018,
      previousChargeAmount = "£1700",
      whoChargePaidBy = "Scheme",
      creditValue = "£1145076",
      debitValue = "£636",
      isSchemePayingCharge = "Yes",
      schemePaymentElectionDate = "Not Applicable",
      schemePayingChargeAmount = "10",
      schemePayingPstr = "schemePstr",
      schemePayingName = "TestSceme",
      schemeDetailsSubSections = Seq(
        SchemeDetailsSubSection(1, "TestName2018", "TestTaxRef"),
        SchemeDetailsSubSection(2, "TestName22018", "TestTaxRef")
      )
    ),
    TaxAdministrationFrameworkSection(
      relatingTo = Period._2019,
      previousChargeAmount = "£1700",
      whoChargePaidBy = "Member",
      creditValue = "£200",
      debitValue = "£25",
      isSchemePayingCharge = "No",
      schemePaymentElectionDate = "Not Applicable",
      schemePayingChargeAmount = "Not Applicable",
      schemePayingPstr = "Not Applicable",
      schemePayingName = "Not Applicable",
      schemeDetailsSubSections = Seq(
        SchemeDetailsSubSection(1, "TestName2019", "TestTaxRef"),
        SchemeDetailsSubSection(2, "TestName22019", "TestTaxRef")
      )
    )
  )

  private val additionalOrHigherReliefSection = AdditionalOrHigherReliefSection(
    amount = "£1000",
    schemePayingName = "SchemeA",
    schemePayingPstr = "schemePstr"
  )

  private val paymentInformationSection = PaymentInformationSection(
    accountName = "TestAccountName",
    sortCode = "TestSortCode",
    accountNumber = "TestAccountNumber"
  )

  private val declarationsSection = DeclarationsSection("Yes", "Yes", "Yes", "No", "No", "Consent given")

  "ViewModelService" - {
    "must correctly create a PDFViewModel using provided CaseIdentifiers and FinalSubmission" in {
      val expectedViewModel = PDFViewModel(
        caseNumber,
        caseIdentificationSection,
        administrativeDetailsSection,
        Some(onBehalfOfSection),
        Some(ltaSection),
        publicSectorSchemeDetailsSections,
        compensationSections,
        taxAdministrationFrameworkSections,
        Some(additionalOrHigherReliefSection),
        Some(paymentInformationSection),
        declarationsSection
      )

      val result = viewModelService.viewModel(caseIdentifiers, finalSubmission)

      result shouldEqual expectedViewModel
    }

    "must correctly create a PDFViewModel using provided CaseIdentifiers and FinalSubmission with LifetimeAllowanceSetup having few fields  = None" in {

      val finalSubmissionCopy = finalSubmission.copy(
        calculationInputs = finalSubmission.calculationInputs.copy(
          setup = finalSubmission.calculationInputs.setup.copy(
            lifetimeAllowanceSetup = Some(
              LifetimeAllowanceSetup(
                Some(true),
                Some(false),
                Some(true),
                None,
                None,
                Some(false),
                Some(true)
              )
            )
          )
        )
      )

      val ltaSectionCopy = ltaSection.copy(increaseInLTACharge = "Not Applicable", newLTACharge = "Not Applicable")

      val expectedViewModel = PDFViewModel(
        caseNumber,
        caseIdentificationSection,
        administrativeDetailsSection,
        Some(onBehalfOfSection),
        Some(ltaSectionCopy),
        publicSectorSchemeDetailsSections,
        compensationSections,
        taxAdministrationFrameworkSections,
        Some(additionalOrHigherReliefSection),
        Some(paymentInformationSection),
        declarationsSection
      )

      val result = viewModelService.viewModel(caseIdentifiers, finalSubmissionCopy)

      result shouldEqual expectedViewModel
    }

    "must correctly create a PDFViewModel using provided CaseIdentifiers and FinalSubmission with LifetimeAllowanceSetup = None" in {

      val finalSubmissionCopy = finalSubmission.copy(
        calculationInputs = finalSubmission.calculationInputs.copy(
          setup = finalSubmission.calculationInputs.setup.copy(
            lifetimeAllowanceSetup = None
          )
        )
      )

      val ltaSectionCopy = ltaSection.copy(
        hadBce = "Not Applicable",
        changeInLtaPercentage = "Not Applicable",
        increaseInLTACharge = "Not Applicable",
        newLTACharge = "Not Applicable",
        multipleBenefitCrystallisationEvent = "Not Applicable"
      )

      val expectedViewModel = PDFViewModel(
        caseNumber,
        caseIdentificationSection,
        administrativeDetailsSection,
        Some(onBehalfOfSection),
        Some(ltaSectionCopy),
        publicSectorSchemeDetailsSections,
        compensationSections,
        taxAdministrationFrameworkSections,
        Some(additionalOrHigherReliefSection),
        Some(paymentInformationSection),
        declarationsSection
      )

      val result = viewModelService.viewModel(caseIdentifiers, finalSubmissionCopy)

      result shouldEqual expectedViewModel
    }

  }
}
