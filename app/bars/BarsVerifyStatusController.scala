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