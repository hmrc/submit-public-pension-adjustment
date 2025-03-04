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

package uk.gov.hmrc.submitpublicpensionadjustment.connectors

import com.github.tomakehurst.wiremock.client.WireMock.*
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.scaladsl.Source
import org.apache.pekko.util.ByteString
import org.mockito.MockitoSugar
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import play.api.Application
import play.api.http.Status.{ACCEPTED, INTERNAL_SERVER_ERROR}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers.{AUTHORIZATION, USER_AGENT}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.submitpublicpensionadjustment.utils.WireMockHelper

import java.time.{LocalDateTime, ZoneId}

class DmsSubmissionConnectorSpec
    extends AnyFreeSpec
    with WireMockHelper
    with MockitoSugar
    with ScalaFutures
    with Matchers
    with IntegrationPatience
    with BeforeAndAfterEach
    with BeforeAndAfterAll {

  implicit private lazy val as: ActorSystem = ActorSystem()

  override def beforeAll(): Unit = {
    super.beforeAll()
    startWireMock()
  }

  override def afterAll(): Unit = {
    stopWireMock()
    as.terminate().futureValue
    super.afterAll()
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    resetWireMock()
  }

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private lazy val app: Application =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.dms-submission.port"          -> wireMockServer.port,
        "microservice.services.dms-submission.callbackUrl"   -> "http://localhost/callback",
        "microservice.services.dms-submission.store"         -> "true",
        "microservice.services.dms-submission.source"        -> "source",
        "microservice.services.dms-submission.formId"        -> "formId",
        "microservice.services.dms-submission.casKey"        -> "casKey",
        "microservice.services.dms-submission.businessArea"  -> "businessArea",
        "internal-auth.token"                                -> "authKey",
        "org.apache.pekko.stream.materializer.subscription-timeout.timeout.mode" -> "warn"
      )
      .build()

  private lazy val connector: DmsSubmissionConnector = app.injector.instanceOf[DmsSubmissionConnector]

  "submit" - {

    val source = Source.single(ByteString.fromString("SomePdfBytes"))

    val nino                = "someNino"
    val submissionReference = "submissionReference"

    val timestamp = LocalDateTime
      .of(2022, 3, 2, 12, 30, 45)
      .atZone(ZoneId.of("UTC"))
      .toInstant

    "must return Done when the server returns ACCEPTED" in {

      wireMockServer.stubFor(
        post(urlEqualTo("/dms-submission/submit"))
          .withHeader(AUTHORIZATION, equalTo("authKey"))
          .withHeader(USER_AGENT, equalTo("submit-public-pension-adjustment"))
          .withMultipartRequestBody(
            aMultipart().withName("submissionReference").withBody(equalTo("submissionReference"))
          )
          .withMultipartRequestBody(aMultipart().withName("callbackUrl").withBody(equalTo("http://localhost/callback")))
          .withMultipartRequestBody(aMultipart().withName("metadata.source").withBody(equalTo("source")))
          .withMultipartRequestBody(
            aMultipart().withName("metadata.timeOfReceipt").withBody(equalTo("2022-03-02T12:30:45"))
          )
          .withMultipartRequestBody(aMultipart().withName("metadata.formId").withBody(equalTo("formId")))
          .withMultipartRequestBody(aMultipart().withName("metadata.customerId").withBody(equalTo("someNino")))
          .withMultipartRequestBody(aMultipart().withName("metadata.classificationType").withBody(equalTo("Queue_1")))
          .withMultipartRequestBody(aMultipart().withName("metadata.businessArea").withBody(equalTo("businessArea")))
          .withMultipartRequestBody(
            aMultipart()
              .withName("form")
              .withBody(equalTo("SomePdfBytes"))
              .withHeader("Content-Disposition", containing("""filename="final-submission.pdf""""))
              .withHeader("Content-Type", equalTo("application/pdf"))
          )
          .willReturn(
            aResponse()
              .withStatus(ACCEPTED)
              .withBody(Json.stringify(Json.obj("id" -> "foobar")))
          )
      )

      connector.submit(nino, source, timestamp, submissionReference, "Queue_1")(hc).futureValue
    }

    "must fail when the server returns another status" in {

      wireMockServer.stubFor(
        post(urlEqualTo("/dms-submission/submit"))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
          )
      )

      connector.submit(nino, source, timestamp, submissionReference, "Queue_1")(hc).failed.futureValue
    }
  }
}
