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

import generators.ModelGenerators
import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.*
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatestplus.mockito.MockitoSugar
import play.api.Logging
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsResult, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.submitpublicpensionadjustment.TestData
import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.FinalSubmission
import uk.gov.hmrc.submitpublicpensionadjustment.models.{AuditMetadata, FinalSubmissionResponse, SubmissionReferences}
import uk.gov.hmrc.submitpublicpensionadjustment.services.FinalSubmissionService

import scala.concurrent.Future

class FinalSubmissionControllerSpec
    extends AnyFreeSpec
    with Matchers
    with OptionValues
    with ModelGenerators
    with MockitoSugar
    with BeforeAndAfterEach
    with Logging {

  private val mockFinalSubmissionService = mock[FinalSubmissionService]
  private val mockAuthConnector          = mock[AuthConnector]

  private val app =
    GuiceApplicationBuilder()
      .overrides(
        bind[FinalSubmissionService].toInstance(mockFinalSubmissionService),
        bind[AuthConnector].toInstance(mockAuthConnector)
      )
      .build()

  override def beforeEach(): Unit = {
    reset(mockFinalSubmissionService, mockAuthConnector)
    super.beforeEach()
  }

  "submit" - {

    "can serialise and deserialize symmetrically" in {
      val serialised                              = Json.prettyPrint(Json.toJson(TestData.finalSubmission))
      val deserialized: JsResult[FinalSubmission] = Json.parse(serialised).validate[FinalSubmission]
      deserialized.get `mustBe` TestData.finalSubmission
    }

    "must submit the final submission and return a submission response" in {

      when(
        mockAuthConnector.authorise[Option[String] ~ Option[AffinityGroup] ~ Option[CredentialRole]](
          any(),
          any()
        )(any(), any())
      ).`thenReturn`(
        Future.successful(
          new ~(new ~(Some("nino"), Some(AffinityGroup.Organisation)), Some(User))
        )
      )

      when(mockFinalSubmissionService.submit(any(), any())(any())) `thenReturn` Future.successful(
        SubmissionReferences("ABCDEF123456", Seq("ABCDEF123456"))
      )

      val expectedMetadata = AuditMetadata(
        userId = "nino",
        affinityGroup = AffinityGroup.Organisation,
        credentialRole = Some(User)
      )

      val finalSubmission = TestData.finalSubmission

      val request =
        FakeRequest(POST, routes.FinalSubmissionController.submit.url)
          .withBody(Json.toJson(finalSubmission))

      val result = route(app, request).value

      status(result) `mustEqual` OK
      contentAsJson(result) `mustEqual` Json.toJson(
        FinalSubmissionResponse("ABCDEF123456")
      )
      verify(mockFinalSubmissionService, times(1)).submit(eqTo(finalSubmission), eqTo(expectedMetadata))(
        any()
      )
    }

    "submit with invalid JSON" - {
      "must return BadRequest when JSON is invalid" in {
        when(
          mockAuthConnector.authorise[Option[String] ~ Option[AffinityGroup] ~ Option[CredentialRole]](
            any(),
            any()
          )(any(), any())
        ).`thenReturn`(
          Future.successful(
            new ~(new ~(Some("nino"), Some(AffinityGroup.Organisation)), Some(User))
          )
        )

        val invalidJson = Json.obj("invalidField" -> "invalidValue")
        val request     = FakeRequest(POST, routes.FinalSubmissionController.submit.url)
          .withBody(invalidJson)
        val result      = route(app, request).value

        status(result) `mustEqual` BAD_REQUEST
        contentAsString(result) must include("Invalid Final Submission")
      }
    }
  }
}
