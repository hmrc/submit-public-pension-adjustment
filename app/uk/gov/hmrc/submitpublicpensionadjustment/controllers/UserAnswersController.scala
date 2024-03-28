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

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.submitpublicpensionadjustment.controllers.actions.IdentifierAction
import uk.gov.hmrc.submitpublicpensionadjustment.repositories.UserAnswersRepository
import uk.gov.hmrc.submitpublicpensionadjustment.models.UserAnswers
import uk.gov.hmrc.submitpublicpensionadjustment.services.UserAnswersService
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class UserAnswersController @Inject() (
  cc: ControllerComponents,
  identify: IdentifierAction,
  repository: UserAnswersRepository,
  userAnswersService: UserAnswersService
)(implicit ec: ExecutionContext)
    extends BackendController(cc) {

  def get: Action[AnyContent] = identify.async { request =>
    repository
      .get(request.internalId)
      .map {
        _.map(userAnswers => Ok(Json.toJson(userAnswers)))
          .getOrElse(NotFound)
      }
  }

  def set: Action[UserAnswers] = identify(parse.json[UserAnswers]).async { request =>
    repository
      .set(request.body)
      .map(_ => NoContent)
  }

  def keepAlive: Action[AnyContent] = identify.async { request =>
    repository
      .keepAlive(request.internalId)
      .map(_ => NoContent)
  }

  def clear: Action[AnyContent] = identify.async { request =>
    repository
      .clear(request.internalId)
      .map(_ => NoContent)
  }

  def checkSubmissionStartedWithId(id: String): Action[AnyContent] = Action.async {
    userAnswersService
      .checkSubmissionStartedWithId(id)
      .map(submissionStarted => Ok(Json.toJson(submissionStarted)))
  }
}
