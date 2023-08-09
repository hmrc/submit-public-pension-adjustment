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

import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchersSugar.eqTo
import org.mockito.MockitoSugar
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.submitpublicpensionadjustment.TestData
import uk.gov.hmrc.submitpublicpensionadjustment.models._
import uk.gov.hmrc.submitpublicpensionadjustment.models.dms.{Compensation, MiniRegime}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FinalSubmissionServiceSpec
    extends AnyFreeSpec
    with Matchers
    with MockitoSugar
    with BeforeAndAfterEach
    with ScalaFutures
    with IntegrationPatience {

  private val mockDmsSubmissionService       = mock[DmsSubmissionService]
  private val mockSubmissionReferenceService = mock[SubmissionReferenceService]
  private val mockAuditService               = mock[AuditService]
  private val mockQueueLogicService          = mock[QueueLogicService]

  private val hc: HeaderCarrier = HeaderCarrier()

  private val service = new FinalSubmissionService(
    mockDmsSubmissionService,
    mockQueueLogicService,
    mockAuditService
  )

  override def beforeEach(): Unit = {
    reset(
      mockDmsSubmissionService,
      mockSubmissionReferenceService,
      mockQueueLogicService,
      mockAuditService
    )
    super.beforeEach()
  }

  "submit" - {

    "must send to DMS and return a submissionReference" in {

      when(mockSubmissionReferenceService.random())
        .thenReturn("submissionReference1")
        .andThenAnswer("submissionReference2")

      val queueReferences =
        Seq(
          QueueReference(Compensation("Compensation_Queue"), "submissionReference1"),
          QueueReference(MiniRegime("MiniRegime_Queue"), "submissionReference2")
        )

      when(
        mockDmsSubmissionService
          .send(
            eqTo(CaseIdentifiers("submissionReference1", queueReferences)),
            any(),
            eqTo("submissionReference1"),
            eqTo("Compensation_Queue")
          )(any())
      )
        .thenReturn(Future.successful(Done))

      when(
        mockDmsSubmissionService
          .send(
            eqTo(CaseIdentifiers("submissionReference2", queueReferences)),
            any(),
            eqTo("submissionReference2"),
            eqTo("MiniRegime_Queue")
          )(any())
      )
        .thenReturn(Future.successful(Done))

      when(mockQueueLogicService.computeQueueReferences(any()))
        .thenReturn(queueReferences)

      when(mockQueueLogicService.determineMostSignificantQueueReference(any())).thenReturn(queueReferences(0))

      val finalSubmission = TestData.finalSubmission

      val auditMetadata = AuditMetadata(
        internalId = "internalId",
        affinityGroup = AffinityGroup.Individual,
        credentialRole = None
      )

      val expectedAudit = FinalSubmissionEvent(
        internalId = "internalId",
        affinityGroup = AffinityGroup.Individual,
        credentialRole = None,
        finalSubmission = finalSubmission
      )

      val result = service.submit(finalSubmission, auditMetadata)(hc).futureValue

      result mustEqual SubmissionReferences("submissionReference1", Seq("submissionReference1", "submissionReference2"))

      verify(mockDmsSubmissionService, times(1))
        .send(
          eqTo(CaseIdentifiers("submissionReference1", queueReferences)),
          any(),
          eqTo("submissionReference1"),
          eqTo("Compensation_Queue")
        )(
          any()
        )

      verify(mockDmsSubmissionService, times(1))
        .send(
          eqTo(CaseIdentifiers("submissionReference2", queueReferences)),
          any(),
          eqTo("submissionReference2"),
          eqTo("MiniRegime_Queue")
        )(
          any()
        )

      verify(mockAuditService, times(2)).auditSubmitRequest(eqTo(expectedAudit))(any())
    }
  }
}
