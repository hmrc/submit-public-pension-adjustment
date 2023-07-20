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

import com.google.inject.Inject
import play.api.Logger
import play.api.mvc.Results.Unauthorized
import play.api.mvc._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}


class AuthorisedSessionAction @Inject()(
                                         val authConnector: AuthConnector,
                                         cc:           MessagesControllerComponents,
                                       )(implicit ec: ExecutionContext) extends ActionBuilder[Request, AnyContent] with AuthorisedFunctions {

  val logger: Logger = Logger(this.getClass)

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequest(request)

    authorised(){
        block(request)
    } recover {
      case _: NoActiveSession =>
        logger.warn(s"no active session")
        Unauthorized("You are not logged in")
      case e: AuthorisationException =>
        logger.info(s"Unauthorised because of ${e.reason}, $e")
        Unauthorized("You do not have access to this service")
    }
  }

  override def parser: BodyParser[AnyContent] = cc.parsers.defaultBodyParser

  override protected def executionContext: ExecutionContext = cc.executionContext
}

