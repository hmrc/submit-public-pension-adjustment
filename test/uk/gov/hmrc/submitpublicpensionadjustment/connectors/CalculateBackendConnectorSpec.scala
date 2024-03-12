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

//
//package uk.gov.hmrc.submitpublicpensionadjustment.connectors
//
//import com.github.tomakehurst.wiremock.client.WireMock._
//import generators.Generators
//import org.scalatest.freespec.AnyFreeSpec
//import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
//import play.api.Application
//import play.api.http.Status.{BAD_REQUEST, OK}
//import play.api.libs.json.Json
//import play.api.test.Helpers.running
//import uk.gov.hmrc.http.HeaderCarrier
//import uk.gov.hmrc.submitpublicpensionadjustment.utils.WireMockHelper
//
//import java.time.LocalDate
//import scala.util.Try
//
//class CalculateBackendConnectorSpec extends AnyFreeSpec with WireMockHelper with ScalaCheckPropertyChecks with Generators {
//
//  implicit lazy val headerCarrier: HeaderCarrier = HeaderCarrier()
//
//  private def application: Application =
//    applicationBuilder()
//      .configure("microservice.services.calculate-public-pension-adjustment.port" -> server.port)
//      .build()
//
//  ".submission" - {
//
//    "must return a RetrieveSubmission response containing data when a known submissionUniqueId is specified" in {
//
//      val url = s"/calculate-public-pension-adjustment/submission"
//      val app = application
//
//      running(app) {
//        val connector = app.injector.instanceOf[CalculateBackendConnector]
//
//        val calculationInputs          = CalculationInputs(Resubmission(false, None), None, None)
//        val retrieveSubmissionResponse = Json.toJson(RetrieveSubmissionResponse(calculationInputs, None)).toString
//
//        val submissionUniqueId = "1234"
//
//        server.stubFor(
//          get(urlEqualTo(url + s"/$submissionUniqueId"))
//            .willReturn(aResponse().withStatus(OK).withBody(retrieveSubmissionResponse))
//        )
//
//        val result: RetrieveSubmissionResponse = connector.retrieveSubmission(UniqueId(submissionUniqueId)).futureValue
//
//        result.calculationInputs mustBe calculationInputs
//        result.calculation mustBe None
//      }
//    }
//
//    "must return a RetrieveSubmission response containing LTA data when a known submissionUniqueId is specified" in {
//
//      val url = s"/calculate-public-pension-adjustment/submission"
//      val app = application
//
//      running(app) {
//        val connector = app.injector.instanceOf[CalculateBackendConnector]
//
//        val calculationInputs          = CalculationInputs(
//          Resubmission(false, None),
//          None,
//          Some(
//            LifeTimeAllowance(
//              true,
//              LocalDate.parse("2018-11-28"),
//              true,
//              ChangeInTaxCharge.IncreasedCharge,
//              LtaProtectionOrEnhancements.Protection,
//              Some(ProtectionType.FixedProtection2014),
//              Some("R41AB678TR23355"),
//              ProtectionEnhancedChanged.Protection,
//              Some(WhatNewProtectionTypeEnhancement.IndividualProtection2016),
//              Some("2134567801"),
//              true,
//              Some(ExcessLifetimeAllowancePaid.Annualpayment),
//              Some(WhoPaidLTACharge.PensionScheme),
//              Some(SchemeNameAndTaxRef("Scheme 1", "00348916RT")),
//              Some(WhoPayingExtraLtaCharge.You),
//              None,
//              NewLifeTimeAllowanceAdditions(
//                false,
//                None,
//                None,
//                None,
//                None,
//                None,
//                None,
//                None,
//                None,
//                None,
//                None,
//                None,
//                None,
//                None,
//                None
//              )
//            )
//          )
//        )
//        val retrieveSubmissionResponse = Json.toJson(RetrieveSubmissionResponse(calculationInputs, None)).toString
//
//        val submissionUniqueId = "1234"
//
//        server.stubFor(
//          get(urlEqualTo(url + s"/$submissionUniqueId"))
//            .willReturn(aResponse().withStatus(OK).withBody(retrieveSubmissionResponse))
//        )
//
//        val result: RetrieveSubmissionResponse = connector.retrieveSubmission(UniqueId(submissionUniqueId)).futureValue
//
//        result.calculationInputs mustBe calculationInputs
//        result.calculation mustBe None
//      }
//    }
//
//    "must return a failed future when the server responds with a BadRequest (i.e. submissionUniqueId is unknown)" in {
//
//      val url = s"/calculate-public-pension-adjustment/submission"
//      val app = application
//
//      running(app) {
//        val connector = app.injector.instanceOf[CalculateBackendConnector]
//
//        val responseBody = Json.toJson("someError").toString
//
//        val submissionUniqueId = "unknownSubmissionUniqueId"
//
//        server.stubFor(
//          get(urlEqualTo(url + s"/$submissionUniqueId"))
//            .willReturn(aResponse().withStatus(BAD_REQUEST).withBody(responseBody))
//        )
//
//        val response: Try[RetrieveSubmissionResponse] =
//          Try(connector.retrieveSubmission(UniqueId("unknownSubmissionUniqueId")).futureValue)
//
//        response.isFailure mustBe true
//      }
//    }
//  }
//}
