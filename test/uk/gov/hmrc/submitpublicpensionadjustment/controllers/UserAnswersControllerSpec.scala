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

import akka.stream.Materializer
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchersSugar.eqTo
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
import uk.gov.hmrc.submitpublicpensionadjustment.models.{Done, UserAnswers}
import uk.gov.hmrc.submitpublicpensionadjustment.repositories.UserAnswersRepository

import java.time.temporal.ChronoUnit
import java.time.{Clock, Instant, ZoneId}
import scala.concurrent.Future

class UserAnswersControllerSpec
    extends AnyFreeSpec
    with Matchers
    with MockitoSugar
    with OptionValues
    with ScalaFutures
    with BeforeAndAfterEach
    with GuiceOneAppPerSuite {

  private val mockRepo          = mock[UserAnswersRepository]
  private val mockAuthConnector = mock[AuthConnector]

  private val instant   = Instant.now.truncatedTo(ChronoUnit.MILLIS)
  private val stubClock = Clock.fixed(instant, ZoneId.systemDefault)
  private val userId    = "foo"
  private val userData  = UserAnswers(userId, Json.obj("bar" -> "baz"), Instant.now(stubClock))

  override def beforeEach(): Unit = {
    reset(mockRepo)
    reset(mockAuthConnector)
    super.beforeEach()
  }

  implicit lazy val mat: Materializer = app.injector.instanceOf[Materializer]

  lazy override val app = new GuiceApplicationBuilder()
    .overrides(
      bind[UserAnswersRepository].toInstance(mockRepo),
      bind[AuthConnector].toInstance(mockAuthConnector)
    )
    .build()

  "UserAnswersController" - {

    ".get" - {

      "must return OK and the data when user data can be found for this session id" in {
        when(mockRepo.get(any[String])) thenReturn Future.successful(Some(userData))
        when(
          mockAuthConnector.authorise[Option[String] ~ Option[String] ~ Option[AffinityGroup] ~ Option[String]](
            any(),
            any()
          )(any(), any())
        )
          .thenReturn(
            Future.successful(
              new ~(new ~(new ~(Some("nino"), Some(userId)), Some(AffinityGroup.Individual)), Some("User"))
            )
          )

        val request =
          FakeRequest(GET, routes.UserAnswersController.get.url)
            .withHeaders("Authorization" -> "Bearer token")

        val controller = app.injector.instanceOf[UserAnswersController]
        val result     = controller.get.apply(request)

        status(result) mustEqual OK
        contentAsJson(result) mustEqual Json.toJson(userData)
      }

      "must return Not Found when user data cannot be found for this session id" in {
        when(mockRepo.get(any[String])) thenReturn Future.successful(None)
        when(
          mockAuthConnector.authorise[Option[String] ~ Option[String] ~ Option[AffinityGroup] ~ Option[String]](
            any(),
            any()
          )(any(), any())
        )
          .thenReturn(
            Future.successful(
              new ~(new ~(new ~(Some("nino"), Some(userId)), Some(AffinityGroup.Individual)), Some("User"))
            )
          )

        val request =
          FakeRequest(GET, routes.UserAnswersController.get.url)
            .withHeaders("Authorization" -> "Bearer token")

        val controller = app.injector.instanceOf[UserAnswersController]
        val result     = controller.get.apply(request)

        status(result) mustEqual NOT_FOUND
      }
    }

    // TO DO Revisit this unit test
//      ".set" - {
//
//        "must return NoContent when the data is successfully saved" in {
//          when(mockRepo.set(any[UserAnswers])) thenReturn Future.successful(Done)
//          when(
//            mockAuthConnector.authorise[Option[String] ~ Option[String] ~ Option[AffinityGroup] ~ Option[String]](
//              any(),
//              any()
//            )(any(), any())
//          )
//            .thenReturn(
//              Future.successful(
//                new ~(new ~(new ~(Some("nino"), Some(userId)), Some(AffinityGroup.Individual)), Some("User"))
//              )
//            )
//
//          val request =
//            FakeRequest(POST, routes.UserAnswersController.set.url)
//              .withHeaders(
//                HeaderNames.xSessionId -> "foo",
//                "Content-Type"         -> "application/json",
//                "Authorization"        -> "Bearer token"
//              )
//              .withJsonBody(Json.toJson(userData))
//
//          val controller = app.injector.instanceOf[UserAnswersController]
//          val result     = controller.set.apply(request)
//
//          status(result) mustEqual NO_CONTENT
//          verify(mockRepo, times(1)).set(eqTo(userData))
//
//        }

    ".keepAlive" - {

      "must return No Content when data is kept alive" in {
        when(mockRepo.keepAlive(any[String])) thenReturn Future.successful(Done)
        when(
          mockAuthConnector.authorise[Option[String] ~ Option[String] ~ Option[AffinityGroup] ~ Option[String]](
            any(),
            any()
          )(any(), any())
        )
          .thenReturn(
            Future.successful(
              new ~(new ~(new ~(Some("nino"), Some(userId)), Some(AffinityGroup.Individual)), Some("User"))
            )
          )

        val request =
          FakeRequest(POST, routes.UserAnswersController.keepAlive.url)
            .withHeaders("Authorization" -> "Bearer token")

        val controller = app.injector.instanceOf[UserAnswersController]
        val result     = controller.keepAlive.apply(request)

        status(result) mustEqual NO_CONTENT
      }
    }

    ".clear" - {

      "must return No Content when data is cleared" in {
        when(mockRepo.clear(any[String])) thenReturn Future.successful(Done)
        when(
          mockAuthConnector.authorise[Option[String] ~ Option[String] ~ Option[AffinityGroup] ~ Option[String]](
            any(),
            any()
          )(any(), any())
        )
          .thenReturn(
            Future.successful(
              new ~(new ~(new ~(Some("nino"), Some(userId)), Some(AffinityGroup.Individual)), Some("User"))
            )
          )

        val request =
          FakeRequest(DELETE, routes.UserAnswersController.clear.url)
            .withHeaders("Authorization" -> "Bearer token")

        val controller = app.injector.instanceOf[UserAnswersController]
        val result     = controller.clear.apply(request)

        status(result) mustEqual NO_CONTENT
      }
    }
    "checkSubmissionStarted" - {
      "Content must be true when record has been found" in {
        when(mockRepo.get(eqTo(userId))) thenReturn Future.successful(Some(userData))

        val request =
          FakeRequest(GET, routes.UserAnswersController.checkSubmissionStartedWithId(userId).url)
            .withHeaders("Authorization" -> "Bearer token")

        val result = route(app, request).value

        status(result) mustEqual OK
        contentAsJson(result) mustEqual Json.toJson(true)
      }

      "Content must be false when record has not been found" in {
        when(mockRepo.get(eqTo(userId))) thenReturn Future.successful(None)

        val request =
          FakeRequest(GET, routes.UserAnswersController.checkSubmissionStartedWithId(userId).url)
            .withHeaders("Authorization" -> "Bearer token")

        val result = route(app, request).value

        status(result) mustEqual OK
        contentAsJson(result) mustEqual Json.toJson(false)
      }
    }
  }
}
