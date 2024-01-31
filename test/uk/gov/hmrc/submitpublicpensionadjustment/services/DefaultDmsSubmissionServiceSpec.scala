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
import play.api.i18n.{Messages, MessagesApi}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.submitpublicpensionadjustment.TestData
import uk.gov.hmrc.submitpublicpensionadjustment.connectors.DmsSubmissionConnector
import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.TaxIdentifiers
import uk.gov.hmrc.submitpublicpensionadjustment.models.{CaseIdentifiers, Done}
import uk.gov.hmrc.submitpublicpensionadjustment.views.xml.FinalSubmissionPdf

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DefaultDmsSubmissionServiceSpec extends AnyFreeSpec with Matchers with ScalaFutures with MockitoSugar {

  private val mockDmsSubmissionConnector = mock[DmsSubmissionConnector]
  private val mockFopService             = mock[FopService]
  private val mockViewModelService       = mock[ViewModelService]
  private val mockFinalSubmissionPdf     = mock[FinalSubmissionPdf]
  private val mockMessagesApi            = mock[MessagesApi]
  private val mockXmlFormatAppendable    = mock[play.twirl.api.XmlFormat.Appendable]

  private implicit val messages: Messages = mockMessagesApi.preferred(Seq.empty)

  when(mockFinalSubmissionPdf.apply(any())(any())).thenReturn(mockXmlFormatAppendable)

  private lazy val service = new DefaultDmsSubmissionService(
    mockDmsSubmissionConnector,
    mockFopService,
    mockViewModelService,
    mockFinalSubmissionPdf,
    mockMessagesApi
  )

  "DefaultDmsSubmissionService" - {

    val finalSubmission            = TestData.finalSubmission
    implicit val hc: HeaderCarrier = HeaderCarrier()

    "should use TRN when NINO is None and TRN is defined" in {
      val caseIdentifiers         = CaseIdentifiers("caseNumber", Seq.empty)
      val trn                     = "someTrn"
      val modifiedFinalSubmission = finalSubmission.copy(
        submissionInputs = finalSubmission.submissionInputs.copy(
          administrativeDetails = finalSubmission.submissionInputs.administrativeDetails.copy(
            claimantDetails = finalSubmission.submissionInputs.administrativeDetails.claimantDetails.copy(
              taxIdentifiers = TaxIdentifiers(None, Some(trn), None)
            )
          )
        )
      )

      when(mockDmsSubmissionConnector.submit(any(), any(), any(), any(), any())(any()))
        .thenReturn(Future.successful(Done))
      when(mockFopService.render(any())).thenReturn(Future.successful(Array.emptyByteArray))
      when(mockViewModelService.viewModel(any(), any())).thenReturn(TestData.viewModel)

      service.send(caseIdentifiers, modifiedFinalSubmission, "submissionReference", "dmsQueueName").futureValue

      verify(mockDmsSubmissionConnector).submit(eqTo(trn), any(), any(), any(), any())(any())
    }

    "should use NINO when NINO is defined and TRN is None" in {
      val caseIdentifiers         = CaseIdentifiers("caseNumber", Seq.empty)
      val nino                    = "someNino"
      val modifiedFinalSubmission = finalSubmission.copy(
        submissionInputs = finalSubmission.submissionInputs.copy(
          administrativeDetails = finalSubmission.submissionInputs.administrativeDetails.copy(
            claimantDetails = finalSubmission.submissionInputs.administrativeDetails.claimantDetails.copy(
              taxIdentifiers = TaxIdentifiers(Some(nino), None, None)
            )
          )
        )
      )

      when(mockDmsSubmissionConnector.submit(any(), any(), any(), any(), any())(any()))
        .thenReturn(Future.successful(Done))
      when(mockFopService.render(any())).thenReturn(Future.successful(Array.emptyByteArray))
      when(mockViewModelService.viewModel(any(), any())).thenReturn(TestData.viewModel)

      service.send(caseIdentifiers, modifiedFinalSubmission, "submissionReference", "dmsQueueName").futureValue

      verify(mockDmsSubmissionConnector).submit(eqTo(nino), any(), any(), any(), any())(any())
    }

    "should use 'Undefined' when both NINO and TRN are None" in {
      val caseIdentifiers         = CaseIdentifiers("caseNumber", Seq.empty)
      val modifiedFinalSubmission = finalSubmission.copy(
        submissionInputs = finalSubmission.submissionInputs.copy(
          administrativeDetails = finalSubmission.submissionInputs.administrativeDetails.copy(
            claimantDetails = finalSubmission.submissionInputs.administrativeDetails.claimantDetails.copy(
              taxIdentifiers = TaxIdentifiers(None, None, None)
            )
          )
        )
      )

      when(mockDmsSubmissionConnector.submit(any(), any(), any(), any(), any())(any()))
        .thenReturn(Future.successful(Done))
      when(mockFopService.render(any())).thenReturn(Future.successful(Array.emptyByteArray))
      when(mockViewModelService.viewModel(any(), any())).thenReturn(TestData.viewModel)

      service.send(caseIdentifiers, modifiedFinalSubmission, "submissionReference", "dmsQueueName").futureValue

      verify(mockDmsSubmissionConnector).submit(eqTo("Undefined"), any(), any(), any(), any())(any())
    }
  }
}
