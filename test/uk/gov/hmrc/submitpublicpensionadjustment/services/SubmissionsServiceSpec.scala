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
import org.mockito.ArgumentMatchersSugar.eqTo
import org.mockito.MockitoSugar
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.submitpublicpensionadjustment.TestData
import uk.gov.hmrc.submitpublicpensionadjustment.models.submission.Submission
import uk.gov.hmrc.submitpublicpensionadjustment.repositories.SubmissionRepository

import java.time.temporal.ChronoUnit
import java.time.{Clock, Instant, ZoneId}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SubmissionsServiceSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaFutures
    with OptionValues
    with MockitoSugar
    with BeforeAndAfterEach {

  private val mockSubmissionRepository = mock[SubmissionRepository]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockSubmissionRepository)
  }
  private val instant             = Instant.now.truncatedTo(ChronoUnit.MILLIS)

  private val service = new SubmissionsService(mockSubmissionRepository)

  private val submissionData =
    Submission("id", "sessionId", "uniqueId", TestData.calculationInputs, None, Instant.parse("2024-03-12T10:00:00Z"))

  "SubmissionsService" - {

    "retrieveSubmissions" - {

      "must return a submission when it exists in the userAnswersRepository" in {
        when(mockSubmissionRepository.get(any())).thenReturn(Future.successful(Some(submissionData)))

        service.retrieveSubmissions("uniqueId").futureValue mustBe Some(submissionData)
        verify(mockSubmissionRepository, times(1)).get(eqTo("uniqueId"))
      }

      "must return None when it does not exist in the repository" in {
        when(mockSubmissionRepository.get("unknownId")).thenReturn(Future.successful(None))

        service.retrieveSubmissions("unknownId").futureValue mustBe None
        verify(mockSubmissionRepository, times(1)).get(eqTo("unknownId"))
      }
    }

    "checkSubmissionsPresentWithUniqueId" - {

      "must return true when it exists in repository" - {
        when(mockSubmissionRepository.get(any())).thenReturn(Future.successful(Some(submissionData)))

        val result = service.checkSubmissionsPresentWithUniqueId("ID")

        result.futureValue mustBe true
      }

      "must return false it does not exists in repository" in {
        when(mockSubmissionRepository.get(any())).thenReturn(Future.successful(None))

        val result = service.checkSubmissionsPresentWithUniqueId("ID")

        result.futureValue mustBe false
      }
    }

  }
}
