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
import uk.gov.hmrc.submitpublicpensionadjustment.models.dms.Compensation
import uk.gov.hmrc.submitpublicpensionadjustment.models.{CaseIdentifiers, QueueReference}
import uk.gov.hmrc.submitpublicpensionadjustment.services.FopService
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.PDFViewModel
import uk.gov.hmrc.submitpublicpensionadjustment.views.xml.FinalSubmissionPdf

import java.nio.file.{Files, Paths}
import scala.concurrent.duration.DurationInt

class PDFViewModelSpec extends AnyFreeSpec with Matchers with Logging {

  implicit val patience: PatienceConfiguration.Timeout = timeout(5.seconds)

  private val app        = GuiceApplicationBuilder().build()
  private val messages   = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())
  private val fopService = app.injector.instanceOf[FopService]

  "PDFViewModel" - {

    "must support pretty printing to aid diagnostics" in {
      val prettyPrintLines = TestData.viewModel.prettyPrint(messages)
      Files.write(Paths.get("test/resources/final_submission.txt"), prettyPrintLines.getBytes())
    }

    "must be constructed from a final submission and generate PDF" in {
      val submissionReference = "submissionReference"
      val dmsQueue            = Compensation("Compensation_Queue")
      val caseIdentifiers     = CaseIdentifiers(submissionReference, Seq(QueueReference(dmsQueue, submissionReference)))

      val viewModel: PDFViewModel = PDFViewModel.build(caseIdentifiers, TestData.finalSubmission)

      checkContent(viewModel)

      val pdfTemplate: FinalSubmissionPdf = app.injector.instanceOf[FinalSubmissionPdf]
      val pdfMarkup: String               = pdfTemplate.render(viewModel, messages).body

      checkFormat(pdfMarkup)

      val pdfOutputFile = fopService.render(pdfMarkup).futureValue(patience)
      Files.write(Paths.get("test/resources/fop/final_submission_populated.pdf"), pdfOutputFile)
    }
  }

  private def checkFormat(pdfMarkup: String) = {
    checkHeadingIncluded(pdfMarkup, "administrativeDetailsSection", "Administrative details")
    checkFieldAndValueIncluded(pdfMarkup, "First name", "FirstName")
  }

  def checkHeadingIncluded(pdfMarkupString: String, sectionId: String, headingValue: String) = {
    val headingBlock =
      s"""<!-- heading --><fo:block role="H3" id="$sectionId" font-size="14pt" font-weight="bold" margin-bottom="0.5cm"> $headingValue</fo:block>"""
    pdfMarkupString must include(headingBlock)
  }

  def checkFieldAndValueIncluded(pdfMarkupString: String, fieldName: String, fieldValue: String) = {
    val fieldNameBlock = s"<!-- key --><fo:block margin-left=\"0cm\" font-weight=\"bold\">$fieldName</fo:block>"
    pdfMarkupString must include(fieldNameBlock)

    val fieldValueBlock = s"<!-- value --><fo:block margin-left=\"0cm\" margin-bottom=\"3mm\">$fieldValue</fo:block>"
    pdfMarkupString must include(fieldValueBlock)
  }

  private def checkContent(viewModel: PDFViewModel) = {
    viewModel.caseNumber mustBe "submissionReference"

    val prettyPrintedOutput = viewModel.prettyPrint(messages)
    val expectedPrettyPrint = Files.readString(Paths.get("test/resources/final_submission_pretty_print.txt"))

    prettyPrintedOutput mustBe expectedPrettyPrint
  }
}
