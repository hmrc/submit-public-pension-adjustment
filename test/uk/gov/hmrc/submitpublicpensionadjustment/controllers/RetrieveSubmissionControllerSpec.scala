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
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.mvc.Result
import play.api.test.Helpers.{GET, defaultAwaitTimeout, status, stubControllerComponents}
import play.api.test.{FakeRequest, Injecting}
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.submitpublicpensionadjustment.controllers.actions.{IdentifierAction, IdentifierRequest}
import uk.gov.hmrc.submitpublicpensionadjustment.services.CalculationDataService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveSubmissionControllerSpec
    extends PlaySpec
    with GuiceOneAppPerTest
    with Injecting
    with MockitoSugar
    with ScalaFutures {

  "RetrieveSubmissionController" when {
    "retrieveSubmissionStatus" should {
      "return OK when the submission status is found and true" in {
        val mockCalculationDataService = mock[CalculationDataService]
        val submissionUniqueId         = "uniqueId"
        when(mockCalculationDataService.retrieveSubmission(any(), any())(any(), any()))
          .thenReturn(Future.successful(true))

        val controller = new RetrieveSubmissionController(
          mockCalculationDataService,
          stubControllerComponents(),
          new FakeIdentifierAction
        )
        val result     = controller.retrieveSubmissionStatus(submissionUniqueId).apply(FakeRequest(GET, "/"))

        status(result) mustBe OK
      }

      "return BadRequest when the submission status is found and false" in {
        val mockCalculationDataService = mock[CalculationDataService]
        val submissionUniqueId         = "uniqueId"
        when(mockCalculationDataService.retrieveSubmission(any(), any())(any(), any()))
          .thenReturn(Future.successful(false))

        val controller = new RetrieveSubmissionController(
          mockCalculationDataService,
          stubControllerComponents(),
          new FakeIdentifierAction
        )
        val result     = controller.retrieveSubmissionStatus(submissionUniqueId).apply(FakeRequest(GET, "/"))

        status(result) mustBe BAD_REQUEST
      }

      class FakeIdentifierAction extends IdentifierAction(null, null)(null) {
        override def invokeBlock[A](
          request: play.api.mvc.Request[A],
          block: IdentifierRequest[A] => Future[Result]
        ): Future[Result] =
          block(IdentifierRequest(request, "nino", AffinityGroup.Individual, None))
      }
    }
  }
}
