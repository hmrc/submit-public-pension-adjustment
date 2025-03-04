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
import com.github.tomakehurst.wiremock.http.Fault
import org.mockito.MockitoSugar
import org.scalatest.concurrent.Eventually.eventually
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import play.api.Application
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.*
import uk.gov.hmrc.submitpublicpensionadjustment.models.submission.RetrieveSubmissionResponse
import uk.gov.hmrc.submitpublicpensionadjustment.models.{CalcUserAnswers, RetrieveSubmissionInfo, UniqueId}
import uk.gov.hmrc.submitpublicpensionadjustment.utils.WireMockHelper

import java.time.Instant

class CalculateBackendConnectorSpec
    extends AnyFreeSpec
    with MockitoSugar
    with ScalaFutures
    with Matchers
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

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private lazy val app: Application = new GuiceApplicationBuilder()
    .configure(
      "microservice.services.calculate-public-pension-adjustment.port" -> wireMockServer.port
    )
    .build()

  private lazy val connector: CalculateBackendConnector = app.injector.instanceOf[CalculateBackendConnector]

  "CalculateBackendConnector" - {

    "retrieveSubmission" - {
      "should return RetrieveSubmissionResponse successfully when calc backend responds with OK" in {
        val retrieveSubmissionInfo = RetrieveSubmissionInfo("internalId", UniqueId("1234"))
        val calculationInputs      = CalculationInputs(
          Resubmission(false, None),
          Setup(
            Some(
              AnnualAllowanceSetup(
                Some(true),
                Some(false),
                Some(false),
                Some(false),
                Some(false),
                Some(false),
                Some(MaybePIAIncrease.No),
                Some(MaybePIAUnchangedOrDecreased.No),
                Some(false),
                Some(false),
                Some(false),
                Some(false)
              )
            ),
            Some(
              LifetimeAllowanceSetup(
                Some(true),
                Some(false),
                Some(true),
                Some(false),
                Some(false),
                Some(false),
                Some(true)
              )
            )
          ),
          None,
          None
        )
        val expectedResponse       = RetrieveSubmissionResponse(calculationInputs, None)

        val url                        = s"/calculate-public-pension-adjustment/retrieve-submission"
        val retrieveSubmissionResponse = Json.toJson(RetrieveSubmissionResponse(calculationInputs, None)).toString
        wireMockServer.stubFor(
          post(url)
            .willReturn(aResponse().withStatus(OK).withBody(retrieveSubmissionResponse))
        )

        eventually(Timeout(Span(30, Seconds))) {
          connector.retrieveSubmissionFromCalcBE(retrieveSubmissionInfo).futureValue shouldBe expectedResponse
        }
      }

      "should throw 404 when calc backend responds with BAD_REQUEST" in {
        val retrieveSubmissionInfo = RetrieveSubmissionInfo("internalId", UniqueId("1234"))
        val url                    = s"/calculate-public-pension-adjustment/retrieve-submission"
        val urlUpdateFlag          = s"/calculate-public-pension-adjustment/submission-status-update"

        val responseBody = Json.toJson("someError").toString

        wireMockServer.stubFor(
          post(url)
            .willReturn(aResponse().withStatus(BAD_REQUEST).withBody(responseBody))
        )

        wireMockServer.stubFor(
          get(urlEqualTo(urlUpdateFlag + s"/${retrieveSubmissionInfo.submissionUniqueId.value}"))
            .willReturn(aResponse().withStatus(OK))
        )

        val response = connector.retrieveCalcUserAnswersFromCalcBE(retrieveSubmissionInfo)

        ScalaFutures.whenReady(response.failed) { response =>
          response                                                shouldBe a[UpstreamErrorResponse]
          response.asInstanceOf[UpstreamErrorResponse].statusCode shouldBe NOT_FOUND
        }
      }

      "should handle unexpected response from retrieve-submission" in {
        val retrieveSubmissionInfo = RetrieveSubmissionInfo("internalId", UniqueId("1234"))
        val url                    = s"/calculate-public-pension-adjustment/retrieve-submission"
        val urlUpdateFlag          =
          s"/calculate-public-pension-adjustment/submission-status-update/${retrieveSubmissionInfo.submissionUniqueId.value}"

        wireMockServer.stubFor(
          post(url)
            .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
        )

        wireMockServer.stubFor(
          get(urlEqualTo(urlUpdateFlag))
            .willReturn(aResponse().withStatus(OK))
        )

        val response = connector.retrieveSubmissionFromCalcBE(retrieveSubmissionInfo)

        ScalaFutures.whenReady(response.failed) { response =>
          response                                                shouldBe a[UpstreamErrorResponse]
          response.asInstanceOf[UpstreamErrorResponse].statusCode shouldBe INTERNAL_SERVER_ERROR
        }
      }

      "should handle failed future for retrieve-submission" in {
        val retrieveSubmissionInfo = RetrieveSubmissionInfo("internalId", UniqueId("1234"))
        val url                    = s"/calculate-public-pension-adjustment/retrieve-submission"
        val urlUpdateFlag          =
          s"/calculate-public-pension-adjustment/submission-status-update/${retrieveSubmissionInfo.submissionUniqueId.value}"

        wireMockServer.stubFor(
          post(url)
            .willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER))
        )

        wireMockServer.stubFor(
          get(urlEqualTo(urlUpdateFlag))
            .willReturn(aResponse().withStatus(OK))
        )

        val response = connector.retrieveSubmissionFromCalcBE(retrieveSubmissionInfo)

        ScalaFutures.whenReady(response.failed) { response =>
          response                                                shouldBe a[UpstreamErrorResponse]
          response.asInstanceOf[UpstreamErrorResponse].statusCode shouldBe INTERNAL_SERVER_ERROR
        }
      }
    }

    "retrieveCalcUserAnswers" - {
      "should return CalcUserAnswers successfully when calc backend responds with OK" in {
        val retrieveSubmissionInfo = RetrieveSubmissionInfo("internalId", UniqueId("1234"))
        val expectedResponse       = CalcUserAnswers(
          "id",
          JsObject(Seq()),
          "uniqueId",
          Instant.ofEpochSecond(1),
          authenticated = true,
          submissionStarted = true
        )
        val url                    = s"/calculate-public-pension-adjustment/retrieve-user-answers"

        wireMockServer.stubFor(
          post(url)
            .withRequestBody(equalToJson(Json.toJson(retrieveSubmissionInfo).toString))
            .willReturn(aResponse().withStatus(OK).withBody(Json.toJson(expectedResponse).toString()))
        )

        connector.retrieveCalcUserAnswersFromCalcBE(retrieveSubmissionInfo).futureValue shouldBe expectedResponse
      }

      "should throw UpstreamErrorResponse when calc backend responds with an error" in {
        val retrieveSubmissionInfo = RetrieveSubmissionInfo("internalId", UniqueId("1234"))
        val url                    = s"/calculate-public-pension-adjustment/retrieve-user-answers"

        wireMockServer.stubFor(
          post(url)
            .willReturn(aResponse().withStatus(BAD_REQUEST))
        )

        val response = connector.retrieveCalcUserAnswersFromCalcBE(retrieveSubmissionInfo)

        ScalaFutures.whenReady(response.failed) { response =>
          response shouldBe a[UpstreamErrorResponse]
        }
      }
    }
  }
}
