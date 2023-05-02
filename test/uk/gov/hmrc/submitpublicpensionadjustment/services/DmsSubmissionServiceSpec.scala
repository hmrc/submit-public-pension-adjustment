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

package uk.gov.hmrc.submitpublicpensionadjustment.services

import akka.stream.Materializer
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.util.ByteString
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
import uk.gov.hmrc.submitpublicpensionadjustment.connectors.DmsSubmissionConnector
import uk.gov.hmrc.submitpublicpensionadjustment.models.Done
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.Calculation
import uk.gov.hmrc.submitpublicpensionadjustment.views.xml.CalculationPdf

import java.time.Instant
import java.time.temporal.ChronoUnit
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

  private lazy val app = GuiceApplicationBuilder()
    .overrides(
      bind[FopService].toInstance(mockFopService),
      bind[DmsSubmissionConnector].toInstance(mockDmsSubmissionConnector)
    )
    .configure(
      "dms-submission.enabled" -> true
    )
    .build()

  private lazy val service                     = app.injector.instanceOf[DmsSubmissionService]
  private lazy val calculationTemplate         = app.injector.instanceOf[CalculationPdf]
  private implicit lazy val mat: Materializer  = app.injector.instanceOf[Materializer]
  private implicit lazy val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq.empty)

  "submitCalculation" - {

    val submissionReference = "submissionReference"
    val now                 = Instant.now.truncatedTo(ChronoUnit.MILLIS)

    val calculation = Calculation(
      nino = "nino",
      dataItem1 = "dataItem1",
      submissionReference = "submissionReference",
      created = now
    )

    val hc: HeaderCarrier = HeaderCarrier()

    "must create a PDF from the calculation and send it to DMS Submission" in {

      val bytes                                               = "PdfBytes".getBytes("UTF-8")
      val sourceCaptor: ArgumentCaptor[Source[ByteString, _]] = ArgumentCaptor.forClass(classOf[Source[ByteString, _]])

      when(mockFopService.render(any())).thenReturn(Future.successful(bytes))
      when(mockDmsSubmissionConnector.submitCalculation(any(), any(), any(), any())(any()))
        .thenReturn(Future.successful(Done))

      val expectedXml = calculationTemplate(calculation).body

      service.submitCalculation(calculation)(hc).futureValue

      verify(mockFopService).render(eqTo(expectedXml))
      verify(mockDmsSubmissionConnector).submitCalculation(
        eqTo("nino"),
        sourceCaptor.capture(),
        eqTo(calculation.created),
        eqTo(submissionReference)
      )(eqTo(hc))

      val result =
        sourceCaptor.getValue().toMat(Sink.fold(ByteString.emptyByteString)(_ ++ _))(Keep.right).run().futureValue

      result.decodeString("UTF-8") mustEqual "PdfBytes"
    }

    "must fail if the fop service fails" in {

      when(mockFopService.render(any())).thenReturn(Future.failed(new RuntimeException()))

      service.submitCalculation(calculation)(hc).failed.futureValue

      verify(mockDmsSubmissionConnector, never).submitCalculation(any(), any(), any(), any())(any())
    }

    "must fail if the dms submission connector fails" in {

      when(mockFopService.render(any())).thenReturn(Future.successful(Array.emptyByteArray))
      when(mockDmsSubmissionConnector.submitCalculation(any(), any(), any(), any())(any()))
        .thenReturn(Future.failed(new RuntimeException()))

      service.submitCalculation(calculation)(hc).failed.futureValue
    }
  }
}
