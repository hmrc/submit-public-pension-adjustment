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

import play.api.libs.json.{JsSuccess, JsValue, Json, Reads}
import play.api.mvc.{Action, ControllerComponents, Request, Result}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.submitpublicpensionadjustment.controllers.actions.{IdentifierAction, IdentifierRequest}
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.CalculationRequest.format
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.{CalculationRequest, CalculationSubmissionResponse}
import uk.gov.hmrc.submitpublicpensionadjustment.models.AuditMetadata
import uk.gov.hmrc.submitpublicpensionadjustment.services.CalculationService

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CalculationController @Inject()(
                                       cc: ControllerComponents,
                                       calculationService: CalculationService,
                                       identify: IdentifierAction
)(implicit ec: ExecutionContext)
    extends BackendController(cc) {

  def submit: Action[JsValue] = identify(parse.json[CalculationRequest]).async(parse.json) { implicit identifiedRequest: IdentifierRequest[JsValue] =>
    withValidJson[CalculationRequest]("Submit Calculation") { claimInput =>
      val nino = identifiedRequest.nino
      calculationService
        .submit(nino, claimInput, getAuditMetadata(identifiedRequest))
        .map { id =>
          Ok(Json.toJson(CalculationSubmissionResponse(id)))
        }
    }
  }

  private def withValidJson[T](
    errMessage: String
  )(f: T => Future[Result])(implicit request: Request[JsValue], reads: Reads[T]): Future[Result] =
    request.body.validate[T] match {
      case JsSuccess(value, _) => f(value)
      case _                   => Future.successful(BadRequest(s"Invalid $errMessage"))
    }

  private def getAuditMetadata(request: IdentifierRequest[_]): AuditMetadata =
    AuditMetadata(
      internalId = request.internalId,
      affinityGroup = request.affinityGroup,
      credentialRole = request.credentialRole
    )
}
