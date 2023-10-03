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

import play.api.Logging
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.internalauth.client._
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendBaseController
import uk.gov.hmrc.submitpublicpensionadjustment.models.dms.{NotificationRequest, SubmissionItemStatus}

import javax.inject.{Inject, Singleton}

@Singleton
class DmsSubmissionCallbackController @Inject() (
  override val controllerComponents: ControllerComponents,
  auth: BackendAuthComponents
) extends BackendBaseController
    with Logging {

  private val predicate = Predicate.Permission(
    resource = Resource(
      resourceType = ResourceType("submit-public-pension-adjustment"),
      resourceLocation = ResourceLocation("dms/callback")
    ),
    action = IAAction("WRITE")
  )

  private val authorised = auth.authorizedAction(predicate)

  def callback = authorised(parse.json[NotificationRequest]) { implicit request =>
    val notification = request.body

    if (notification.status == SubmissionItemStatus.Failed) {
      logger.error(
        s"DMS notification received - failed with error : ${notification.failureReason.getOrElse("")}"
      )
    } else {
      logger.info(s"DMS notification received with status ${notification.status}")
    }

    Ok
  }
}
