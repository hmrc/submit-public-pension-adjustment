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

package uk.gov.hmrc.submitpublicpensionadjustment.controllers

import org.apache.pekko.stream.Materializer
import org.mockito.ArgumentMatchers.any
import org.mockito.MockitoSugar
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.auth.core.{AffinityGroup, AuthConnector}
import uk.gov.hmrc.submitpublicpensionadjustment.models.{CalcUserAnswers, Done}
import uk.gov.hmrc.submitpublicpensionadjustment.repositories.CalcUserAnswersRepository

import java.time.temporal.ChronoUnit
import java.time.{Clock, Instant, ZoneId}
import scala.concurrent.Future

class CalcUserAnswersControllerSpec
    extends AnyFreeSpec
    with Matchers
    with MockitoSugar
    with OptionValues
    with ScalaFutures
    with BeforeAndAfterEach
    with GuiceOneAppPerSuite {

  private val mockRepo          = mock[CalcUserAnswersRepository]
  private val mockAuthConnector = mock[AuthConnector]

  private val instant   = Instant.now.truncatedTo(ChronoUnit.MILLIS)
  private val stubClock = Clock.fixed(instant, ZoneId.systemDefault)
  private val userId    = "foo"
  private val uniqueId  = "unique"
  private val userData  = CalcUserAnswers(userId, Json.obj("bar" -> "baz"), uniqueId, Instant.now(stubClock))

  override def beforeEach(): Unit = {
    reset(mockRepo)
    reset(mockAuthConnector)
    super.beforeEach()
  }

  implicit lazy val mat: Materializer = app.injector.instanceOf[Materializer]

  lazy override val app = new GuiceApplicationBuilder()
    .overrides(
      bind[CalcUserAnswersRepository].toInstance(mockRepo),
      bind[AuthConnector].toInstance(mockAuthConnector)
    )
    .build()

  "CalcUserAnswersController" - {

    ".getById" - {

      "must return OK and the data when user data can be found for this session id" in {
        when(mockRepo.get(userId)) thenReturn Future.successful(Some(userData))
        when(
          mockAuthConnector.authorise[Option[String] ~ Option[AffinityGroup] ~ Option[String]](
            any(),
            any()
          )(any(), any())
        )
          .thenReturn(
            Future.successful(
              new ~(new ~(Some(userId), Some(AffinityGroup.Individual)), Some("User"))
            )
          )

        val request =
          FakeRequest(GET, routes.CalcUserAnswersController.getById(userId).url)
            .withHeaders("Authorization" -> "Bearer token")

        val controller = app.injector.instanceOf[CalcUserAnswersController]
        val result     = controller.getById(userId) apply request

        status(result) mustEqual OK
        contentAsJson(result) mustEqual Json.toJson(userData)
      }

      "must return NO_CONTENT when user data cannot be found for this session id" in {
        when(mockRepo.get(userId)) thenReturn Future.successful(None)
        when(
          mockAuthConnector.authorise[Option[String] ~ Option[AffinityGroup] ~ Option[String]](
            any(),
            any()
          )(any(), any())
        )
          .thenReturn(
            Future.successful(
              new ~(new ~(Some("nino"), Some(AffinityGroup.Individual)), Some("User"))
            )
          )

        val request =
          FakeRequest(GET, routes.CalcUserAnswersController.getById(userId).url)
            .withHeaders("Authorization" -> "Bearer token")

        val controller = app.injector.instanceOf[CalcUserAnswersController]
        val result     = controller.getById(userId).apply(request)

        status(result) mustEqual NO_CONTENT
      }
    }

    ".getByUniqueId" - {

      "must return OK and the data when user data can be found for this session id" in {
        when(mockRepo.getByUniqueId(uniqueId)) thenReturn Future.successful(Some(userData))
        when(
          mockAuthConnector.authorise[Option[String] ~ Option[AffinityGroup] ~ Option[String]](
            any(),
            any()
          )(any(), any())
        )
          .thenReturn(
            Future.successful(
              new ~(new ~(Some(userId), Some(AffinityGroup.Individual)), Some("User"))
            )
          )

        val request =
          FakeRequest(GET, routes.CalcUserAnswersController.getByUniqueId(uniqueId).url)
            .withHeaders("Authorization" -> "Bearer token")

        val controller = app.injector.instanceOf[CalcUserAnswersController]
        val result     = controller.getByUniqueId(uniqueId) apply request

        status(result) mustEqual OK
        contentAsJson(result) mustEqual Json.toJson(userData)
      }

      "must return NO_CONTENT when user data cannot be found for this session id" in {
        when(mockRepo.getByUniqueId(uniqueId)) thenReturn Future.successful(None)
        when(
          mockAuthConnector.authorise[Option[String] ~ Option[AffinityGroup] ~ Option[String]](
            any(),
            any()
          )(any(), any())
        )
          .thenReturn(
            Future.successful(
              new ~(new ~(Some("nino"), Some(AffinityGroup.Individual)), Some("User"))
            )
          )

        val request =
          FakeRequest(GET, routes.CalcUserAnswersController.getByUniqueId(uniqueId).url)
            .withHeaders("Authorization" -> "Bearer token")

        val controller = app.injector.instanceOf[CalcUserAnswersController]
        val result     = controller.getByUniqueId(uniqueId).apply(request)

        status(result) mustEqual NO_CONTENT
      }
    }

    ".clear" - {

      "must return No Content when data is cleared" in {
        when(mockRepo.clear(any[String])) thenReturn Future.successful(Done)
        when(
          mockAuthConnector.authorise[Option[String] ~ Option[AffinityGroup] ~ Option[String]](
            any(),
            any()
          )(any(), any())
        )
          .thenReturn(
            Future.successful(
              new ~(new ~(Some("nino"), Some(AffinityGroup.Individual)), Some("User"))
            )
          )

        val request =
          FakeRequest(DELETE, routes.CalcUserAnswersController.clear.url)
            .withHeaders("Authorization" -> "Bearer token")

        val controller = app.injector.instanceOf[CalcUserAnswersController]
        val result     = controller.clear.apply(request)

        status(result) mustEqual NO_CONTENT
      }
    }

  }
}
