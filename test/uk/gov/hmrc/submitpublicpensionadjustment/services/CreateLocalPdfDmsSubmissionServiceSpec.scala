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

import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.MockitoSugar
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import play.api.i18n.MessagesApi
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.submitpublicpensionadjustment.TestData
import uk.gov.hmrc.submitpublicpensionadjustment.models.{CaseIdentifiers, Done}
import uk.gov.hmrc.submitpublicpensionadjustment.views.xml.FinalSubmissionPdf

import java.nio.file.{Files, Paths}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CreateLocalPdfDmsSubmissionServiceSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaFutures
    with OptionValues
    with MockitoSugar
    with BeforeAndAfterEach {

  private val mockFopService              = mock[FopService]
  private val mockViewModelService        = mock[ViewModelService]
  private val mockFinalSubmissionPdf      = mock[FinalSubmissionPdf]
  private val mockMessagesApi             = mock[MessagesApi]

  private lazy val service = new CreateLocalPdfDmsSubmissionService(
    mockFopService,
    mockViewModelService,
    mockFinalSubmissionPdf,
    mockMessagesApi
  )

  "CreateLocalPdfDmsSubmissionService" - {

    val caseIdentifiers            = CaseIdentifiers("caseNumber", Seq.empty)
    val finalSubmission            = TestData.finalSubmission
    val submissionReference        = "submissionReference"
    val dmsQueueName               = "dmsQueueName"
    implicit val hc: HeaderCarrier = HeaderCarrier()

    "must successfully create a PDF and write to a file" in {
      val pdfBytes = "PdfContent".getBytes("UTF-8")
      val filePath = Paths.get(s"test/output/${caseIdentifiers.caseNumber}.pdf")

      when(mockFopService.render(any())).thenReturn(Future.successful(pdfBytes))
      when(mockViewModelService.viewModel(any(), any())).thenReturn(TestData.viewModel)
      when(mockFinalSubmissionPdf.apply(any())(any())).thenReturn(mock[play.twirl.api.XmlFormat.Appendable])

      val result = service.send(caseIdentifiers, finalSubmission, submissionReference, dmsQueueName).futureValue

      verify(mockFopService).render(any())
      verify(mockViewModelService).viewModel(eqTo(caseIdentifiers), eqTo(finalSubmission))

      Files.exists(filePath) mustBe true
      Files.readAllBytes(filePath) mustEqual pdfBytes
      result mustBe Done
    }
  }
}
