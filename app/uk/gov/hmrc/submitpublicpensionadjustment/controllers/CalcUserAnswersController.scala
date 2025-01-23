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
import uk.gov.hmrc.submitpublicpensionadjustment.repositories.CalcUserAnswersRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CalcUserAnswersController @Inject() (
  cc: ControllerComponents,
  identify: IdentifierAction,
  calcUserAnswers: CalcUserAnswersRepository
)(implicit ec: ExecutionContext)
    extends BackendController(cc) {

  def getById(id: String): Action[AnyContent] = identify.async { request =>
    calcUserAnswers.get(id).map {
      _.map(userAnswers => Ok(Json.toJson(userAnswers)))
        .getOrElse(NoContent)
    }
  }

  def getByUniqueId(uniqueId: String): Action[AnyContent] = identify.async { request =>
    calcUserAnswers.getByUniqueId(uniqueId).map {
      _.map(userAnswers => Ok(Json.toJson(userAnswers)))
        .getOrElse(NoContent)
    }
  }

  def clear: Action[AnyContent] = identify.async { request =>
    calcUserAnswers
      .clear(request.userId)
      .map(_ => NoContent)
  }
}
