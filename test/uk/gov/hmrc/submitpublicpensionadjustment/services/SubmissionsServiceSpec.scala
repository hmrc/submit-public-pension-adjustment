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

import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.submitpublicpensionadjustment.TestData
import uk.gov.hmrc.submitpublicpensionadjustment.models.submission.Submission
import uk.gov.hmrc.submitpublicpensionadjustment.repositories.SubmissionRepository

import java.time.Instant
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

  private val service = new SubmissionsService(mockSubmissionRepository)

  private val submissionData =
    Submission("id", "uniqueId", TestData.calculationInputs, None, Instant.parse("2024-03-12T10:00:00Z"))

  "SubmissionsService" - {

    "retrieveSubmissions" - {

      "must return a submission when it exists in the userAnswersRepository" in {
        when(mockSubmissionRepository.get(any())).`thenReturn`(Future.successful(Some(submissionData)))

        service.retrieveSubmissions("uniqueId").futureValue `mustBe` Some(submissionData)
        verify(mockSubmissionRepository, times(1)).get(eqTo("uniqueId"))
      }

      "must return None when it does not exist in the repository" in {
        when(mockSubmissionRepository.get("unknownId")).`thenReturn`(Future.successful(None))

        service.retrieveSubmissions("unknownId").futureValue `mustBe` None
        verify(mockSubmissionRepository, times(1)).get(eqTo("unknownId"))
      }
    }

    "retrieveSubmissionsById" - {

      "must return a submission when it exists in the userAnswersRepository" in {
        when(mockSubmissionRepository.getByUserId(any())).`thenReturn`(Future.successful(Some(submissionData)))

        service.retrieveSubmissionsById("id").futureValue `mustBe` Some(submissionData)
        verify(mockSubmissionRepository, times(1)).getByUserId(eqTo("id"))
      }

      "must return None when it does not exist in the repository" in {
        when(mockSubmissionRepository.getByUserId("unknownId")).`thenReturn`(Future.successful(None))

        service.retrieveSubmissionsById("unknownId").futureValue `mustBe` None
        verify(mockSubmissionRepository, times(1)).getByUserId(eqTo("unknownId"))
      }
    }

    "checkSubmissionsPresentWithUniqueId" - {

      "must return true when it exists in repository" - {
        when(mockSubmissionRepository.get(any())).`thenReturn`(Future.successful(Some(submissionData)))

        val result = service.checkSubmissionsPresentWithUniqueId("ID")

        result.futureValue `mustBe` true
      }

      "must return false it does not exists in repository" in {
        when(mockSubmissionRepository.get(any())).`thenReturn`(Future.successful(None))

        val result = service.checkSubmissionsPresentWithUniqueId("ID")

        result.futureValue `mustBe` false
      }
    }

    "checkSubmissionsPresentWithId" - {

      "must return true when it exists in repository" - {
        when(mockSubmissionRepository.getByUserId(any())).`thenReturn`(Future.successful(Some(submissionData)))

        val result = service.checkSubmissionsPresentWithId("ID")

        result.futureValue `mustBe` true
      }

      "must return false it does not exists in repository" in {
        when(mockSubmissionRepository.getByUserId(any())).`thenReturn`(Future.successful(None))

        val result = service.checkSubmissionsPresentWithId("ID")

        result.futureValue `mustBe` false
      }
    }

  }
}
