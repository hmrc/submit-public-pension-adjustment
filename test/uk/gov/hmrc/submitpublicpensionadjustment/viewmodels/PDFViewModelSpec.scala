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
import uk.gov.hmrc.submitpublicpensionadjustment.models.{CaseIdentifiers, QueueReference}
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.response.Period
import uk.gov.hmrc.submitpublicpensionadjustment.models.dms.Compensation
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
      val submissionReference = "submissionReference"
      val dmsQueue = Compensation("Compensation_Queue")
      val caseIdentifiers = CaseIdentifiers(submissionReference, Seq(QueueReference(dmsQueue, submissionReference)))
      val viewModel: PDFViewModel = PDFViewModel.build(caseIdentifiers, TestData.finalSubmission)

      viewModel.caseNumber mustBe submissionReference

      viewModel.administrativeDetailsSection mustBe AdministrativeDetailsSection(
        firstName = "FirstName",
        surname = "Surname",
        dob = "13/01/1920",
        addressLine1 = "testLine1",
        addressLine2 = "testLine2",
        postCode = "Postcode",
        country = None,
        utr = None,
        ninoOrTrn = "someNino",
        contactNumber = ""
      )

      viewModel.publicSectorSchemeDetailsSections mustBe Seq(
        PublicSectorSchemeDetailsSection(
          schemeName = "TestScheme",
          pstr = "TestPSTR",
          individualSchemeReference = "reformReference"
        )
      )

      viewModel.compensationSections mustBe Seq(
        CompensationSection(
          relatingTo = Period.Year(2017),
          directAmount = "100",
          indirectAmount = "200",
          revisedTaxChargeTotal = "270",
          chargeYouPaid = "50",
          chargeSchemePaid = "75",
          originalSchemePaidChargeName = "Scheme A",
          originalSchemePaidChargePstr = "PSTR123"
        )
      )

      viewModel.additionalOrHigherReliefSection mustBe Some(
        AdditionalOrHigherReliefSection(
          amount = "1000",
          schemePayingName = "SchemeA",
          schemePayingPstr = "schemePstr"
        )
      )

      viewModel.onBehalfOfSection mustBe Some(
        OnBehalfOfSection(
          firstName = "FirstName",
          surname = "Surname",
          dob = "13/01/1920",
          addressLine1 = "Behalf Address 1",
          addressLine2 = "Behalf Address 2",
          postCode = "Postcode",
          country = None,
          utr = Some("someUTR"),
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

      viewModel.declarationsSection mustBe DeclarationsSection("Y", "Y", "Y", "Y", "Y", "N", "N")

      val view      = app.injector.instanceOf[FinalSubmissionPdf]
      val xmlString = view.render(viewModel, messages).body
      val result    = fopService.render(xmlString).futureValue(patience)

      val fileName = "test/resources/fop/final_submission_populated.pdf"

      Files.write(Paths.get(fileName), result)
    }
  }
}
