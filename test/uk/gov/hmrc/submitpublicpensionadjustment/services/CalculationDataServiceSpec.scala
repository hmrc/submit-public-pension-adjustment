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

import org.mockito.ArgumentMatchers.any
import org.mockito.{ArgumentMatchers, MockitoSugar}
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.submitpublicpensionadjustment.connectors.CalculateBackendConnector
import uk.gov.hmrc.submitpublicpensionadjustment.models.{Done, RetrieveSubmissionInfo, UniqueId}
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.{CalculationInputs, ChangeInTaxCharge, ExcessLifetimeAllowancePaid, LifeTimeAllowance, LtaProtectionOrEnhancements, NewLifeTimeAllowanceAdditions, ProtectionEnhancedChanged, ProtectionType, Resubmission, SchemeNameAndTaxRef, WhatNewProtectionTypeEnhancement, WhoPaidLTACharge, WhoPayingExtraLtaCharge}
import uk.gov.hmrc.submitpublicpensionadjustment.models.submission.RetrieveSubmissionResponse
import uk.gov.hmrc.submitpublicpensionadjustment.repositories.SubmissionRepository

import java.time.LocalDate
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class CalculationDataServiceSpec extends AnyFreeSpec with MockitoSugar {

  implicit lazy val headerCarrier: HeaderCarrier = HeaderCarrier()

  private val mockCalculateBackendConnector = mock[CalculateBackendConnector]
  private val mockSubmissionRepository      = mock[SubmissionRepository]

  private val service = new CalculationDataService(mockCalculateBackendConnector, mockSubmissionRepository)

  "Submission Retrieval" - {

    "result should true when submission can be retrieved and inserted" in {

      val retrieveSubmissionInfo = RetrieveSubmissionInfo("internalId", UniqueId("1234"))

      val retrieveSubmissionResponse =
        RetrieveSubmissionResponse(CalculationInputs(Resubmission(false, None), None, None), None)

      when(
        mockCalculateBackendConnector.retrieveSubmission(ArgumentMatchers.eq(retrieveSubmissionInfo))(
          ArgumentMatchers.eq(headerCarrier)
        )
      )
        .thenReturn(Future.successful(retrieveSubmissionResponse))

      when(mockSubmissionRepository.insert(any())).thenReturn(Future.successful(Done))

      val result: Future[Boolean] =
        service.retrieveSubmission("internalId", retrieveSubmissionInfo.submissionUniqueId.value)(
          implicitly[ExecutionContext],
          implicitly(headerCarrier)
        )

      result.futureValue mustBe true
    }

    "result should true when submission with LTA data can be retrieved and inserted" in {

      val retrieveSubmissionInfo = RetrieveSubmissionInfo("internalId", UniqueId("1234"))

      val retrieveSubmissionResponse = RetrieveSubmissionResponse(
        CalculationInputs(
          Resubmission(false, None),
          None,
          Some(
            LifeTimeAllowance(
              true,
              LocalDate.parse("2018-11-28"),
              true,
              ChangeInTaxCharge.IncreasedCharge,
              LtaProtectionOrEnhancements.Protection,
              Some(ProtectionType.FixedProtection2014),
              Some("R41AB678TR23355"),
              ProtectionEnhancedChanged.Protection,
              Some(WhatNewProtectionTypeEnhancement.IndividualProtection2016),
              Some("2134567801"),
              true,
              Some(ExcessLifetimeAllowancePaid.Annualpayment),
              Some(WhoPaidLTACharge.PensionScheme),
              Some(SchemeNameAndTaxRef("Scheme 1", "00348916RT")),
              Some(WhoPayingExtraLtaCharge.You),
              None,
              NewLifeTimeAllowanceAdditions(
                false,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None
              )
            )
          )
        ),
        None
      )

      when(
        mockCalculateBackendConnector.retrieveSubmission(ArgumentMatchers.eq(retrieveSubmissionInfo))(
          ArgumentMatchers.eq(headerCarrier)
        )
      )
        .thenReturn(Future.successful(retrieveSubmissionResponse))

      when(mockSubmissionRepository.insert(any())).thenReturn(Future.successful(Done))

      val result: Future[Boolean] =
        service.retrieveSubmission("internalId", retrieveSubmissionInfo.submissionUniqueId.value)(
          implicitly[ExecutionContext],
          implicitly(headerCarrier)
        )

      result.futureValue mustBe true
    }

    "result should be false when submission can be retrieved but cannot be inserted" in {

      val retrieveSubmissionInfo = RetrieveSubmissionInfo("internalId", UniqueId("1234"))

      val retrieveSubmissionResponse =
        RetrieveSubmissionResponse(CalculationInputs(Resubmission(false, None), None, None), None)

      when(
        mockCalculateBackendConnector.retrieveSubmission(ArgumentMatchers.eq(retrieveSubmissionInfo))(
          ArgumentMatchers.eq(headerCarrier)
        )
      )
        .thenReturn(Future.successful(retrieveSubmissionResponse))

      val exception = new RuntimeException("insert failed")
      when(mockSubmissionRepository.insert(any())).thenReturn(Future.failed(exception))

      val result = service
        .retrieveSubmission("internalId", retrieveSubmissionInfo.submissionUniqueId.value)(
          implicitly[ExecutionContext],
          implicitly(headerCarrier)
        )
        .futureValue

      result mustBe false
    }

    "result should be false when submission cannot be retrieved" in {

      val retrieveSubmissionInfo = RetrieveSubmissionInfo("internalId", UniqueId("1234"))

      val exception = new RuntimeException("retrieval failed")

      when(
        mockCalculateBackendConnector.retrieveSubmission(ArgumentMatchers.eq(retrieveSubmissionInfo))(
          ArgumentMatchers.eq(headerCarrier)
        )
      )
        .thenReturn(Future.failed(exception))

      val result = service
        .retrieveSubmission("internalId", retrieveSubmissionInfo.submissionUniqueId.value)(
          implicitly[ExecutionContext],
          implicitly(headerCarrier)
        )
        .futureValue

      result mustBe false
    }
  }
}
