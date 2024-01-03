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

import org.mockito.ArgumentMatchers.any
import org.mockito.MockitoSugar
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.internalauth.client.test.{BackendAuthComponentsStub, StubBehaviour}
import uk.gov.hmrc.internalauth.client._
import uk.gov.hmrc.submitpublicpensionadjustment.models.dms.{NotificationRequest, SubmissionItemStatus}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DmsSubmissionCallbackControllerSpec
    extends AnyFreeSpec
    with Matchers
    with OptionValues
    with ScalaFutures
    with MockitoSugar
    with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockStubBehaviour)
  }

  private val mockStubBehaviour         = mock[StubBehaviour]
  private val stubBackendAuthComponents =
    BackendAuthComponentsStub(mockStubBehaviour)(stubControllerComponents(), implicitly)

  private val app = GuiceApplicationBuilder()
    .overrides(
      bind[BackendAuthComponents].toInstance(stubBackendAuthComponents)
    )
    .build()

  private val predicate = Predicate.Permission(
    Resource(ResourceType("submit-public-pension-adjustment"), ResourceLocation("dms/callback")),
    IAAction("WRITE")
  )

  private val notification = NotificationRequest(
    id = "id",
    status = SubmissionItemStatus.Processed,
    failureReason = None
  )

  "callback" - {

    "must return OK when a valid request is received" in {

      when(mockStubBehaviour.stubAuth[Unit](any(), any())).thenReturn(Future.unit)

      val request = FakeRequest(POST, routes.DmsSubmissionCallbackController.callback.url)
        .withHeaders(AUTHORIZATION -> "Some auth token")
        .withBody(Json.toJson(notification))

      val result = route(app, request).value
      status(result) mustEqual OK

      verify(mockStubBehaviour).stubAuth(Some(predicate), Retrieval.EmptyRetrieval)
    }

    "must return BAD_REQUEST when an invalid request is received" in {

      when(mockStubBehaviour.stubAuth[Unit](any(), any())).thenReturn(Future.unit)

      val request = FakeRequest(POST, routes.DmsSubmissionCallbackController.callback.url)
        .withHeaders(AUTHORIZATION -> "Some auth token")
        .withBody(Json.obj())

      val result = route(app, request).value
      status(result) mustEqual BAD_REQUEST
    }

    "must fail for an unauthenticated user" in {

      val request = FakeRequest(POST, routes.DmsSubmissionCallbackController.callback.url)
        .withBody(Json.toJson(notification)) // No Authorization header

      route(app, request).value.failed.futureValue
    }

    "must fail when the user is not authorised" in {

      when(mockStubBehaviour.stubAuth[Unit](any(), any())).thenReturn(Future.failed(new RuntimeException()))

      val request = FakeRequest(POST, routes.DmsSubmissionCallbackController.callback.url)
        .withHeaders(AUTHORIZATION -> "Some auth token")
        .withBody(Json.toJson(notification))

      route(app, request).value.failed.futureValue
    }
  }
}
