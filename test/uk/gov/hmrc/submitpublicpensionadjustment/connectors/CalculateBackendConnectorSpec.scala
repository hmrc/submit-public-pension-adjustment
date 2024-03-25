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

package uk.gov.hmrc.submitpublicpensionadjustment.connectors

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import generators.Generators
import org.scalatest.concurrent.Eventually.eventually
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.{a, convertToAnyMustWrapper}
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers.running
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}
import uk.gov.hmrc.submitpublicpensionadjustment.models.UniqueId
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs._
import uk.gov.hmrc.submitpublicpensionadjustment.models.submission.RetrieveSubmissionResponse

class CalculateBackendConnectorSpec
    extends AnyFlatSpec
    with ScalaCheckPropertyChecks
    with Generators
    with BeforeAndAfterAll
    with BeforeAndAfterEach {
  implicit lazy val headerCarrier: HeaderCarrier = HeaderCarrier()

  val wiremockStubPort = 12800

  val server: WireMockServer = new WireMockServer(wireMockConfig().port(wiremockStubPort))

  private val application = GuiceApplicationBuilder()
    .build()

  override def beforeAll(): Unit = {
    server.start()
    super.beforeAll()
  }

  override def beforeEach(): Unit = {
    server.resetAll()
    super.beforeEach()
  }

  override def afterAll(): Unit = {
    super.afterAll()
    server.stop()
  }

  "CalculateBackendConnector" should "retrieve a submission response when call repsonse OK " in {

    val url = s"/calculate-public-pension-adjustment/submission"

    running(application) {
      val connector = application.injector.instanceOf[CalculateBackendConnector]

      val calculationInputs          = CalculationInputs(Resubmission(false, None), None, None)
      val retrieveSubmissionResponse = Json.toJson(RetrieveSubmissionResponse(calculationInputs, None)).toString

      val submissionUniqueId = "1234"

      server.stubFor(
        get(urlEqualTo(url + s"/$submissionUniqueId"))
          .willReturn(aResponse().withStatus(OK).withBody(retrieveSubmissionResponse))
      )

      eventually(Timeout(Span(30, Seconds))) {
        val result: RetrieveSubmissionResponse =
          connector.retrieveSubmission(UniqueId(submissionUniqueId)).futureValue
        result.calculationInputs mustBe calculationInputs
        result.calculation mustBe None
      }
    }
  }

  "CalculateBackendConnector" should "receive upstream error response when retrieveSubmission Bad Request but updateSubmissionFlag OK" in {

    val url = s"/calculate-public-pension-adjustment/submission"

    val urlUpdateFlag = s"/calculate-public-pension-adjustment/submission-status-update"

    running(application) {
      val connector = application.injector.instanceOf[CalculateBackendConnector]

      val responseBody = Json.toJson("someError").toString

      val submissionUniqueId = "1234"

      server.stubFor(
        get(urlEqualTo(url + s"/$submissionUniqueId"))
          .willReturn(aResponse().withStatus(BAD_REQUEST).withBody(responseBody))
      )

      server.stubFor(
        get(urlEqualTo(urlUpdateFlag + s"/$submissionUniqueId"))
          .willReturn(aResponse().withStatus(OK))
      )

      eventually(Timeout(Span(30, Seconds))) {
        val response = connector.retrieveSubmission(UniqueId(submissionUniqueId))

        ScalaFutures.whenReady(response.failed) { response =>
          response shouldBe a[UpstreamErrorResponse]
        }
      }

    }
  }

  "CalculateBackendConnector" should "receive upstream error response when retrieveSubmission Bad Request and updateSubmissionFlag Bad Request" in {

    val url = s"/calculate-public-pension-adjustment/submission"

    val urlUpdateFlag = s"/calculate-public-pension-adjustment/submission-status-update"

    running(application) {
      val connector = application.injector.instanceOf[CalculateBackendConnector]

      val responseBody = Json.toJson("someError").toString

      val submissionUniqueId = "1234"

      server.stubFor(
        get(urlEqualTo(url + s"/$submissionUniqueId"))
          .willReturn(aResponse().withStatus(BAD_REQUEST).withBody(responseBody))
      )

      server.stubFor(
        get(urlEqualTo(urlUpdateFlag + s"/$submissionUniqueId"))
          .willReturn(aResponse().withStatus(BAD_REQUEST))
      )

      eventually(Timeout(Span(30, Seconds))) {
        val response = connector.retrieveSubmission(UniqueId(submissionUniqueId))

        ScalaFutures.whenReady(response.failed) { response =>
          response shouldBe a[UpstreamErrorResponse]
        }
      }

    }
  }
}
