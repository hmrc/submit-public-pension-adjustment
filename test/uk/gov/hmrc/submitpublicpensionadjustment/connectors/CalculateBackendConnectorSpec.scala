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
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers.running
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.submitpublicpensionadjustment.models.UniqueId
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs._
import uk.gov.hmrc.submitpublicpensionadjustment.models.submission.RetrieveSubmissionResponse

import scala.util.Try

class CalculateBackendConnectorSpec
    extends AnyFreeSpec
    with ScalaCheckPropertyChecks
    with Generators
    with BeforeAndAfterAll
    with BeforeAndAfterEach {
  implicit lazy val headerCarrier: HeaderCarrier = HeaderCarrier()

  private val application = GuiceApplicationBuilder().build()

  val wiremockStubPort = 12802

  val server: WireMockServer = new WireMockServer(wireMockConfig().port(wiremockStubPort))

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

  ".submission" - {

    "must return a RetrieveSubmission response containing data when a known submissionUniqueId is specified" in {

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

    "must return a failed future when the server responds with an error" in {

      val url = s"/calculate-public-pension-adjustment/submission"

      running(application) {
        val connector = application.injector.instanceOf[CalculateBackendConnector]

        val responseBody = Json.toJson("someError").toString

        val submissionUniqueId = "1234"

        server.stubFor(
          get(urlEqualTo(url + s"/$submissionUniqueId"))
            .willReturn(aResponse().withStatus(BAD_REQUEST).withBody(responseBody))
        )

        eventually(Timeout(Span(30, Seconds))) {
          val response: Try[RetrieveSubmissionResponse] =
            Try(connector.retrieveSubmission(UniqueId(submissionUniqueId)).futureValue)

          response.isFailure mustBe true
        }
      }
    }
  }
}
