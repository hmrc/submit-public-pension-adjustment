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

import org.apache.pekko.stream.Materializer
import org.apache.pekko.stream.scaladsl.{Keep, Sink, Source}
import org.apache.pekko.util.ByteString
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.{ArgumentCaptor, MockitoSugar}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.submitpublicpensionadjustment.TestData
import uk.gov.hmrc.submitpublicpensionadjustment.connectors.DmsSubmissionConnector
import uk.gov.hmrc.submitpublicpensionadjustment.models.dms.Compensation
import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.FinalSubmission
import uk.gov.hmrc.submitpublicpensionadjustment.models.{CaseIdentifiers, Done, QueueReference}
import uk.gov.hmrc.submitpublicpensionadjustment.views.xml.FinalSubmissionPdf

import scala.concurrent.Future

class DmsSubmissionServiceSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaFutures
    with OptionValues
    with MockitoSugar
    with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockFopService, mockDmsSubmissionConnector)
  }

  private val mockFopService             = mock[FopService]
  private val mockDmsSubmissionConnector = mock[DmsSubmissionConnector]
  private val mockViewModelService       = mock[ViewModelService]

  private lazy val app = GuiceApplicationBuilder()
    .overrides(
      bind[FopService].toInstance(mockFopService),
      bind[DmsSubmissionConnector].toInstance(mockDmsSubmissionConnector),
      bind[ViewModelService].toInstance(mockViewModelService)
    )
    .configure(
      "dms-submission.enabled" -> true
    )
    .build()

  private lazy val service                     = app.injector.instanceOf[DmsSubmissionService]
  private lazy val finalSubmissionTemplate     = app.injector.instanceOf[FinalSubmissionPdf]
  private implicit lazy val mat: Materializer  = app.injector.instanceOf[Materializer]
  private implicit lazy val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq.empty)

  "finalSubmission" - {

    val submissionReference = "submissionReference"
    val dmsQueue            = Compensation("Compensation_Queue")
    val caseIdentifiers     = CaseIdentifiers(submissionReference, Seq(QueueReference(dmsQueue, submissionReference)))

    val finalSubmission = TestData.finalSubmission

    val hc: HeaderCarrier = HeaderCarrier()

    "must create a PDF from the FinalSubmission and send it to DMS Submission" in {

      val bytes                                               = "PdfBytes".getBytes("UTF-8")
      val sourceCaptor: ArgumentCaptor[Source[ByteString, _]] = ArgumentCaptor.forClass(classOf[Source[ByteString, _]])

      when(mockFopService.render(any())).thenReturn(Future.successful(bytes))
      when(mockDmsSubmissionConnector.submit(any(), any(), any(), any(), any())(any()))
        .thenReturn(Future.successful(Done))
      when(mockViewModelService.viewModel(any(), any())).thenReturn(TestData.viewModel)

      val expectedXml = finalSubmissionTemplate(TestData.viewModel).body

      service.send(caseIdentifiers, finalSubmission, submissionReference, dmsQueue.queueName)(hc).futureValue

      verify(mockViewModelService).viewModel(eqTo(caseIdentifiers), eqTo(finalSubmission))
      verify(mockFopService).render(eqTo(expectedXml))
      verify(mockDmsSubmissionConnector).submit(
        eqTo("someNino"),
        sourceCaptor.capture(),
        any(),
        eqTo(submissionReference),
        eqTo(dmsQueue.queueName)
      )(eqTo(hc))

      val result =
        sourceCaptor.getValue().toMat(Sink.fold(ByteString.emptyByteString)(_ ++ _))(Keep.right).run().futureValue

      result.decodeString("UTF-8") mustEqual "PdfBytes"
    }

    "must fail if the fop service fails" in {

      when(mockFopService.render(any())).thenReturn(Future.failed(new RuntimeException()))

      service.send(caseIdentifiers, finalSubmission, submissionReference, dmsQueue.queueName)(hc).failed.futureValue

      verify(mockDmsSubmissionConnector, never).submit(any(), any(), any(), any(), any())(any())
    }

    "must fail if the dms submission connector fails" in {

      when(mockFopService.render(any())).thenReturn(Future.successful(Array.emptyByteArray))
      when(mockDmsSubmissionConnector.submit(any(), any(), any(), any(), any())(any()))
        .thenReturn(Future.failed(new RuntimeException()))

      service.send(caseIdentifiers, finalSubmission, submissionReference, dmsQueue.queueName)(hc).failed.futureValue
    }
  }

  "NoOpDmsSubmissionService should return done after performing an operation" - {
    val service = new NoOpDmsSubmissionService()

    val caseIdentifiers            = CaseIdentifiers("caseNumber", Seq(QueueReference(null, "submissionReference")))
    val finalSubmission            = FinalSubmission(null, None, null)
    val submissionReference        = "submissionReference"
    val dmsQueueName               = "dmsQueueName"
    implicit val hc: HeaderCarrier = HeaderCarrier()

    val result: Future[Done] = service.send(caseIdentifiers, finalSubmission, submissionReference, dmsQueueName)

    whenReady(result) { done =>
      done mustBe Done
    }
  }

  "DmsSubmissionService bindings" - {

    "must bind to CreateLocalPdfDmsSubmissionService when dms-submission.createLocalPdf is true" in {
      val app = GuiceApplicationBuilder()
        .overrides(
          bind[FopService].toInstance(mockFopService),
          bind[DmsSubmissionConnector].toInstance(mockDmsSubmissionConnector),
          bind[ViewModelService].toInstance(mockViewModelService)
        )
        .configure(
          "dms-submission.enabled"        -> false,
          "dms-submission.createLocalPdf" -> true
        )
        .build()

      val service = app.injector.instanceOf[DmsSubmissionService]
      service mustBe a[CreateLocalPdfDmsSubmissionService]
    }

    "must bind to NoOpDmsSubmissionService when dms-submission.createLocalPdf is false" in {
      val app = GuiceApplicationBuilder()
        .overrides(
          bind[FopService].toInstance(mockFopService),
          bind[DmsSubmissionConnector].toInstance(mockDmsSubmissionConnector),
          bind[ViewModelService].toInstance(mockViewModelService)
        )
        .configure(
          "dms-submission.enabled"        -> false,
          "dms-submission.createLocalPdf" -> false
        )
        .build()

      val service = app.injector.instanceOf[DmsSubmissionService]
      service mustBe a[NoOpDmsSubmissionService]
    }
  }
}
