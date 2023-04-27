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
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.{Calculation, CalculationRequest, CalculationSubmissionEvent}
import uk.gov.hmrc.submitpublicpensionadjustment.models.{AuditMetadata, Done}
import uk.gov.hmrc.submitpublicpensionadjustment.services.{AuditService, CalculationService, DmsSubmissionService, SubmissionReferenceService}

import java.time.{Clock, Instant, ZoneId}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CalculationServiceSpec
    extends AnyFreeSpec
    with Matchers
    with MockitoSugar
    with BeforeAndAfterEach
    with ScalaFutures
    with IntegrationPatience {

  private val mockDmsSubmissionService       = mock[DmsSubmissionService]
  private val mockSubmissionReferenceService = mock[SubmissionReferenceService]
  private val mockAuditService               = mock[AuditService]
  private val fixedInstant                   = Instant.now
  private val fixedClock                     = Clock.fixed(fixedInstant, ZoneId.systemDefault())

  private val hc: HeaderCarrier = HeaderCarrier()

  private val service = new CalculationService(
    mockDmsSubmissionService,
    mockSubmissionReferenceService,
    mockAuditService,
    fixedClock
  )

  override def beforeEach(): Unit = {
    reset(
      mockDmsSubmissionService,
      mockSubmissionReferenceService,
      mockAuditService
    )
    super.beforeEach()
  }

  "submit" - {

    "must submit a calculation and return a submissionReference" in {

      when(mockSubmissionReferenceService.random()).thenReturn("submissionReference")
      when(mockDmsSubmissionService.submitCalculation(any())(any())).thenReturn(Future.successful(Done))

      val calculationRequest = CalculationRequest(
        "dataItem1"
      )

      val auditMetadata = AuditMetadata(
        internalId = "internalId",
        affinityGroup = AffinityGroup.Individual,
        credentialRole = None
      )

      val expectedCalculation = Calculation(
        nino = "nino",
        dataItem1 = "dataItem1",
        submissionReference = "submissionReference",
        created = fixedInstant
      )

      val expectedAudit = CalculationSubmissionEvent(
        internalId = "internalId",
        affinityGroup = AffinityGroup.Individual,
        credentialRole = None,
        calculation = expectedCalculation
      )

      val result = service.submit("nino", calculationRequest, auditMetadata)(hc).futureValue

      result mustEqual "submissionReference"
      verify(mockDmsSubmissionService, times(1)).submitCalculation(eqTo(expectedCalculation))(any())
      verify(mockAuditService, times(1)).auditSubmitRequest(eqTo(expectedAudit))(any())
    }
  }
}
