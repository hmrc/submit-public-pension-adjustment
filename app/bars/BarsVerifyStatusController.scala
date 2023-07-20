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

package bars

import com.google.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.submitpublicpensionadjustment.controllers.AuthorisedSessionAction
import uk.gov.hmrc.submitpublicpensionadjustment.models.bars.BarsUpdateVerifyStatusParams

import scala.concurrent.ExecutionContext

@Singleton
class BarsVerifyStatusController @Inject() (
                                             auth: AuthorisedSessionAction,
                                             barsService: BarsVerifyStatusService,
                                             cc: ControllerComponents
                                           )
                                           (implicit exec: ExecutionContext)
  extends BackendController(cc) {

  def status(): Action[BarsUpdateVerifyStatusParams] = auth.async(parse.json[BarsUpdateVerifyStatusParams]) { implicit request =>
    barsService.status(request.body.taxId)
      .map { resp => Ok(Json.toJson(resp)) }
  }

  def update(): Action[BarsUpdateVerifyStatusParams] = auth.async(parse.json[BarsUpdateVerifyStatusParams]) { implicit request =>
    barsService.update(request.body.taxId)
      .map { resp => Ok(Json.toJson(resp)) }
  }

}