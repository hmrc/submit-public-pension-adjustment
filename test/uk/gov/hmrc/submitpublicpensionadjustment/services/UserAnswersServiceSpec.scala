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
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.submitpublicpensionadjustment.models.{Done, UserAnswers}
import uk.gov.hmrc.submitpublicpensionadjustment.repositories.UserAnswersRepository

import java.time.Instant
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserAnswersServiceSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaFutures
    with OptionValues
    with MockitoSugar
    with BeforeAndAfterEach {

  private val mockUserAnswersRepository = mock[UserAnswersRepository]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswersRepository)
  }

  private val hc: HeaderCarrier = HeaderCarrier()

  private val service = new UserAnswersService(mockUserAnswersRepository)

  "UserAnswersService" - {

    "retrieveUserAnswers" - {

      "must return a submission when it exists in the userAnswersRepository" in {

        val userAnswers = UserAnswers("id", Json.obj("foo" -> "bar"), Instant.ofEpochSecond(1))

        when(mockUserAnswersRepository.get(any())).thenReturn(Future.successful(Some(userAnswers)))

        service.retrieveUserAnswers("uniqueId").futureValue mustBe Some(userAnswers)
        verify(mockUserAnswersRepository, times(1)).get(eqTo("uniqueId"))
      }

      "must return None when it does not exist in the repository" in {
        when(mockUserAnswersRepository.get("unknownId")).thenReturn(Future.successful(None))

        service.retrieveUserAnswers("unknownId").futureValue mustBe None
        verify(mockUserAnswersRepository, times(1)).get(eqTo("unknownId"))
      }
    }

    "checkSubmissionStarted" - {

      "must return true when it exists in repository" - {
        val userAnswers = new UserAnswers("id", Json.obj("foo" -> "bar"), Instant.ofEpochSecond(1))

        when(mockUserAnswersRepository.get(any())).thenReturn(Future.successful(Some(userAnswers)))

        val result = service.checkUserAnswersPresentWithId("ID")

        result.futureValue mustBe true
      }

      "must return false it does not exists in repository" in {
        when(mockUserAnswersRepository.get(any())).thenReturn(Future.successful(None))

        val result = service.checkUserAnswersPresentWithId("ID")

        result.futureValue mustBe false
      }
    }

    "clearById" - {

      "must clear a UserAnswer when it exists in the repository" in {

        when(mockUserAnswersRepository.clear("id")).thenReturn(Future.successful(Done))
        service.clearById("id").futureValue mustBe Done
      }

    }

  }
}
