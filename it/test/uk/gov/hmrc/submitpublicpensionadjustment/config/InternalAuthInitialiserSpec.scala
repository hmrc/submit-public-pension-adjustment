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

package uk.gov.hmrc.submitpublicpensionadjustment.config

import com.github.tomakehurst.wiremock.client.WireMock.*
import org.scalatest.concurrent.Eventually.eventually
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import play.api.http.Status.{CREATED, INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers.AUTHORIZATION
import uk.gov.hmrc.submitpublicpensionadjustment.utils.WireMockHelper

class InternalAuthInitialiserSpec
  extends AnyFreeSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with WireMockHelper
    with BeforeAndAfterEach
    with BeforeAndAfterAll {

  override def beforeAll(): Unit = {
    super.beforeAll()
    startWireMock()
  }

  override def afterAll(): Unit = {
    stopWireMock()
    super.afterAll()
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    resetWireMock()
  }

  "when configured to run" - {

    "must initialise the internal-auth token if it is not already initialised" in {

      val authToken = "authToken"
      val appName   = "appName"

      val expectedRequest = Json.obj(
        "token"       -> authToken,
        "principal"   -> appName,
        "permissions" -> Seq(
          Json.obj(
            "resourceType"     -> "dms-submission",
            "resourceLocation" -> "submit",
            "actions"          -> List("WRITE")
          )
        )
      )

      wireMockServer.stubFor(
        get(urlMatching("/test-only/token"))
          .willReturn(aResponse().withStatus(NOT_FOUND))
      )

      wireMockServer.stubFor(
        post(urlMatching("/test-only/token"))
          .willReturn(aResponse().withStatus(CREATED))
      )

      val app = GuiceApplicationBuilder()
        .configure(
          "microservice.services.internal-auth.port" -> wireMockServer.port(),
          "appName"                                  -> appName,
          "internal-auth-token-initialiser.enabled"  -> true,
          "internal-auth.token"                      -> authToken
        )
        .build()

      val initialiser = app.injector.instanceOf[InternalAuthTokenInitialiser]
      initialiser.initialised.futureValue

      eventually(Timeout(Span(60, Seconds))) {
        wireMockServer.verify(
          1,
          getRequestedFor(urlMatching("/test-only/token"))
            .withHeader(AUTHORIZATION, equalTo(authToken))
        )
        wireMockServer.verify(
          1,
          postRequestedFor(urlMatching("/test-only/token"))
            .withRequestBody(equalToJson(Json.stringify(Json.toJson(expectedRequest))))
        )
      }
    }

    "must not initialise the internal-auth token if it is already initialised" in {

      val authToken = "authToken"
      val appName   = "appName"

      wireMockServer.stubFor(
        get(urlMatching("/test-only/token"))
          .willReturn(aResponse().withStatus(OK))
      )

      wireMockServer.stubFor(
        post(urlMatching("/test-only/token"))
          .willReturn(aResponse().withStatus(CREATED))
      )

      val app = GuiceApplicationBuilder()
        .configure(
          "microservice.services.internal-auth.port" -> wireMockServer.port(),
          "appName"                                  -> appName,
          "internal-auth-token-initialiser.enabled"  -> true,
          "internal-auth.token"                      -> authToken
        )
        .build()

      val initialiser = app.injector.instanceOf[InternalAuthTokenInitialiser]
      initialiser.initialised.futureValue

      eventually(Timeout(Span(60, Seconds))) {
        wireMockServer.verify(
          1,
          getRequestedFor(urlMatching("/test-only/token"))
            .withHeader(AUTHORIZATION, equalTo(authToken))
        )
        wireMockServer.verify(0, postRequestedFor(urlMatching("/test-only/token")))
      }
    }

    "must fail to initialise the internal-auth token if the creation fails" in {

      val authToken = "authToken"
      val appName   = "appName"

      wireMockServer.stubFor(
        get(urlMatching("/test-only/token"))
          .willReturn(aResponse().withStatus(NOT_FOUND))
      )

      wireMockServer.stubFor(
        post(urlMatching("/test-only/token"))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      val app = GuiceApplicationBuilder()
        .configure(
          "microservice.services.internal-auth.port" -> wireMockServer.port(),
          "appName"                                  -> appName,
          "internal-auth-token-initialiser.enabled"  -> true,
          "internal-auth.token"                      -> authToken
        )
        .build()

      val exception = intercept[RuntimeException] {
        app.injector.instanceOf[InternalAuthTokenInitialiser].initialised.futureValue
      }

      exception.getMessage mustBe "The future returned an exception of type: java.lang.RuntimeException, with message: Unable to add dms-submission grants."
    }

    "must fail to add dms-submission grants if the addition fails" in {

      val authToken = "authToken"
      val appName   = "appName"

      wireMockServer.stubFor(
        get(urlMatching("/test-only/token"))
          .willReturn(aResponse().withStatus(NOT_FOUND))
      )

      wireMockServer.stubFor(
        post(urlMatching("/test-only/token"))
          .inScenario("Add DMS Submission Grants")
          .whenScenarioStateIs("STARTED")
          .willReturn(aResponse().withStatus(CREATED))
          .willSetStateTo("Grants Stage")
      )

      wireMockServer.stubFor(
        post(urlMatching("/test-only/token"))
          .inScenario("Add DMS Submission Grants")
          .whenScenarioStateIs("Grants Stage")
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      val app = GuiceApplicationBuilder()
        .configure(
          "microservice.services.internal-auth.port" -> wireMockServer.port(),
          "appName"                                  -> appName,
          "internal-auth-token-initialiser.enabled"  -> true,
          "internal-auth.token"                      -> authToken
        )
        .build()

      val exception = intercept[RuntimeException] {
        app.injector.instanceOf[InternalAuthTokenInitialiser].initialised.futureValue
      }

      exception.getMessage mustBe "The future returned an exception of type: java.lang.RuntimeException, with message: Unable to add dms-submission grants."
    }
  }

  "when not configured to run" - {

    "must not make the relevant calls to internal-auth" in {

      val authToken = "authToken"
      val appName   = "appName"

      wireMockServer.stubFor(
        get(urlMatching("/test-only/token"))
          .willReturn(aResponse().withStatus(OK))
      )

      wireMockServer.stubFor(
        post(urlMatching("/test-only/token"))
          .willReturn(aResponse().withStatus(CREATED))
      )

      val app = GuiceApplicationBuilder()
        .configure(
          "microservice.services.internal-auth.port" -> wireMockServer.port(),
          "appName"                                  -> appName,
          "internal-auth-token-initialiser.enabled"  -> false,
          "internal-auth.token"                      -> authToken
        )
        .build()

      app.injector.instanceOf[InternalAuthTokenInitialiser].initialised.futureValue

      wireMockServer.verify(0, getRequestedFor(urlMatching("/test-only/token")))
      wireMockServer.verify(0, postRequestedFor(urlMatching("/test-only/token")))
    }
  }
}
