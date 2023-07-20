/*
 * Copyright 2023 HM Revenue & Customs
 *
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

