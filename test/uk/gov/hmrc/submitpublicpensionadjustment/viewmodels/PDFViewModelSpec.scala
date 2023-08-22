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

package uk.gov.hmrc.submitpublicpensionadjustment.viewmodels

import org.scalatest.concurrent.Futures.timeout
import org.scalatest.concurrent.PatienceConfiguration
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.Logging
import play.api.i18n.MessagesApi
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import uk.gov.hmrc.submitpublicpensionadjustment.TestData
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.response.{Period => ResponsePeriod}
import uk.gov.hmrc.submitpublicpensionadjustment.models.dms.Compensation
import uk.gov.hmrc.submitpublicpensionadjustment.models.{CaseIdentifiers, QueueReference}
import uk.gov.hmrc.submitpublicpensionadjustment.services.FopService
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.PDFViewModel
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.sections._
import uk.gov.hmrc.submitpublicpensionadjustment.views.xml.FinalSubmissionPdf

import java.nio.file.{Files, Paths}
import scala.concurrent.duration.DurationInt

class PDFViewModelSpec extends AnyFreeSpec with Matchers with Logging {
  implicit val patience: PatienceConfiguration.Timeout = timeout(5.seconds)
  private val app                                      = GuiceApplicationBuilder().build()
  private val messages                                 = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())
  private val fopService                               = app.injector.instanceOf[FopService]

  "PDFViewModel" - {

    "must support pretty printing to aid diagnostics" in {

      val prettyPrintLines = TestData.viewModel.prettyPrint(messages)
      logger.info(s"pdfViewModel :\n$prettyPrintLines\n")

      val fileName = "test/resources/final_submission.txt"
      Files.write(Paths.get(fileName), prettyPrintLines.getBytes())
    }

    "must be constructed from a final submission and generate PDF" in {
      val submissionReference     = "submissionReference"
      val dmsQueue                = Compensation("Compensation_Queue")
      val caseIdentifiers         = CaseIdentifiers(submissionReference, Seq(QueueReference(dmsQueue, submissionReference)))
      val viewModel: PDFViewModel = PDFViewModel.build(caseIdentifiers, TestData.finalSubmission)

      viewModel.caseNumber mustBe submissionReference

      viewModel.administrativeDetailsSection mustBe AdministrativeDetailsSection(
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
        utr = "Not Entered",
        ninoOrTrn = "someNino",
        contactNumber = "Not Entered"
      )

      viewModel.publicSectorSchemeDetailsSections mustBe Seq(
        PublicSectorSchemeDetailsSection(
          schemeName = "TestScheme",
          pstr = "TestPSTR",
          reformReference = "reformReference",
          legacyReference = "Not Applicable"
        )
      )

      viewModel.compensationSections mustBe Seq(
        CompensationSection(
          relatingTo = ResponsePeriod.Year(2017),
          directAmount = "£100",
          indirectAmount = "£200",
          revisedTaxChargeTotal = "£270",
          chargeYouPaid = "£50",
          additionalRows = Seq(
            ("scheme", "1"),
            ("chargeSchemePaid", "£100"),
            ("originalSchemePaidChargeName", "TestName2017"),
            ("originalSchemePaidChargePstr", "TestTaxRef2017"),
            ("scheme", "2"),
            ("chargeSchemePaid", "£100"),
            ("originalSchemePaidChargeName", "TestName2222017"),
            ("originalSchemePaidChargePstr", "TestTaxRef")
          )
        ),
        CompensationSection(
          ResponsePeriod.Year(2018),
          "£1002018",
          "£2002018",
          "£2702018",
          "£502018",
          Seq(
            ("scheme", "1"),
            ("chargeSchemePaid", "£100"),
            ("originalSchemePaidChargeName", "TestName2018"),
            ("originalSchemePaidChargePstr", "TestTaxRef"),
            ("scheme", "2"),
            ("chargeSchemePaid", "£100"),
            ("originalSchemePaidChargeName", "TestName22018"),
            ("originalSchemePaidChargePstr", "TestTaxRef")
          )
        )
      )

      viewModel.additionalOrHigherReliefSection mustBe Some(
        AdditionalOrHigherReliefSection(
          amount = "£1000",
          schemePayingName = "SchemeA",
          schemePayingPstr = "schemePstr"
        )
      )

      viewModel.taxAdministrationFrameworkSections mustBe Seq(
        TaxAdministrationFrameworkSection(
          relatingTo = ResponsePeriod._2017,
          previousChargeAmount = "£300",
          whoChargePaidBy = "Scheme",
          additionalRows = Seq(
            ("scheme", "1"),
            ("previousChargePaidBySchemeName", "TestName2017"),
            ("previousChargePaidByPstr", "TestTaxRef2017"),
            ("scheme", "2"),
            ("previousChargePaidBySchemeName", "TestName2222017"),
            ("previousChargePaidByPstr", "TestTaxRef")
          ),
          creditValue = "£50",
          debitValue = "£25",
          isSchemePayingCharge = "Yes",
          schemePaymentElectionDate = "Not Applicable",
          schemePayingChargeAmount = "10",
          schemePayingPstr = "schemePstr",
          schemePayingName = "TestSceme"
        ),
        TaxAdministrationFrameworkSection(
          relatingTo = ResponsePeriod._2018,
          previousChargeAmount = "£1700",
          whoChargePaidBy = "None",
          additionalRows = Seq(
            ("scheme", "1"),
            ("previousChargePaidBySchemeName", "TestName2018"),
            ("previousChargePaidByPstr", "TestTaxRef"),
            ("scheme", "2"),
            ("previousChargePaidBySchemeName", "TestName22018"),
            ("previousChargePaidByPstr", "TestTaxRef")
          ),
          creditValue = "£3526",
          debitValue = "£636",
          isSchemePayingCharge = "No",
          schemePaymentElectionDate = "Not Applicable",
          schemePayingChargeAmount = "Not Applicable",
          schemePayingPstr = "Not Applicable",
          schemePayingName = "Not Applicable"
        )
      )

      viewModel.onBehalfOfSection mustBe Some(
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

      viewModel.paymentInformationSection mustBe Some(
        PaymentInformationSection(
          accountName = "TestAccountName",
          sortCode = "TestSortCode",
          accountNumber = "TestAccountNumber"
        )
      )

      viewModel.declarationsSection mustBe DeclarationsSection("Y", "Y", "Y", "N", "N")

      val view      = app.injector.instanceOf[FinalSubmissionPdf]
      val xmlString = view.render(viewModel, messages).body
      val result    = fopService.render(xmlString).futureValue(patience)

      val fileName = "test/resources/fop/final_submission_populated.pdf"

      Files.write(Paths.get(fileName), result)
    }
  }
}
