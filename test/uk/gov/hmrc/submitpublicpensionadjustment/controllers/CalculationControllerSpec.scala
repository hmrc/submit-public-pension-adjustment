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

package uk.gov.hmrc.submitpublicpensionadjustment.controllers

import generators.ModelGenerators
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchersSugar.eqTo
import org.mockito.MockitoSugar
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.submitpublicpensionadjustment.controllers.routes
import uk.gov.hmrc.submitpublicpensionadjustment.models.AuditMetadata
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.{CalculationRequest, CalculationSubmissionResponse}
import uk.gov.hmrc.submitpublicpensionadjustment.services.CalculationService

import scala.concurrent.Future

class CalculationControllerSpec
    extends AnyFreeSpec
    with Matchers
    with OptionValues
    with ModelGenerators
    with MockitoSugar
    with BeforeAndAfterEach {

  private val mockCalculationService = mock[CalculationService]
  private val mockAuthConnector      = mock[AuthConnector]

  private val app =
    GuiceApplicationBuilder()
      .overrides(
        bind[CalculationService].toInstance(mockCalculationService),
        bind[AuthConnector].toInstance(mockAuthConnector)
      )
      .build()

  override def beforeEach(): Unit = {
    reset(mockCalculationService, mockAuthConnector)
    super.beforeEach()
  }

  "submit" - {

    "must submit the calculation and return a submission response" in {

      when(
        mockAuthConnector.authorise[Option[String] ~ Option[String] ~ Option[AffinityGroup] ~ Option[CredentialRole]](
          any(),
          any()
        )(any(), any())
      )
        .thenReturn(
          Future.successful(
            new ~(new ~(new ~(Some("nino"), Some("internalId")), Some(AffinityGroup.Organisation)), Some(User))
          )
        )
      when(mockCalculationService.submit(any(), any(), any())(any())) thenReturn Future.successful(
        "submissionReference"
      )

      val expectedMetadata = AuditMetadata(
        internalId = "internalId",
        affinityGroup = AffinityGroup.Organisation,
        credentialRole = Some(User)
      )

      val calculationRequest = CalculationRequest(
        dataItem1 = "dataItem1"
      )

      val request =
        FakeRequest(POST, routes.CalculationController.submit.url)
          .withBody(Json.toJson(calculationRequest))

      val result = route(app, request).value

      status(result) mustEqual OK
      contentAsJson(result) mustEqual Json.toJson(CalculationSubmissionResponse("submissionReference"))
      verify(mockCalculationService, times(1)).submit(eqTo("nino"), eqTo(calculationRequest), eqTo(expectedMetadata))(
        any()
      )
    }
  }
}
