package uk.gov.hmrc.submitpublicpensionadjustment.connectors

import play.api.mvc.RequestHeader
import uk.gov.hmrc.http.HttpClient
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.submitpublicpensionadjustment.models.bars.{BarsUpdateVerifyStatusParams, BarsVerifyStatusResponse, TaxIdKey}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BarsVerifyStatusConnector(httpClient: HttpClient, baseUrl: String)(implicit ec: ExecutionContext) {

  // POST to keep the TaxId out of the url
  def status(taxId: TaxIdKey)(implicit request: RequestHeader): Future[BarsVerifyStatusResponse] =
    httpClient.POST[BarsUpdateVerifyStatusParams, BarsVerifyStatusResponse](s"$baseUrl/submit-public-pension-adjustment/bars/verify/status", BarsUpdateVerifyStatusParams(taxId))

  def update(taxId: TaxIdKey)(implicit request: RequestHeader): Future[BarsVerifyStatusResponse] =
    httpClient.POST[BarsUpdateVerifyStatusParams, BarsVerifyStatusResponse](s"$baseUrl/submit-public-pension-adjustment/bars/verify/update", BarsUpdateVerifyStatusParams(taxId))

  @Inject()
  def this(httpClient: HttpClient, servicesConfig: ServicesConfig)(implicit ec: ExecutionContext) = this(
    httpClient,
    servicesConfig.baseUrl("submit-public-pension-adjustment")
  )
}