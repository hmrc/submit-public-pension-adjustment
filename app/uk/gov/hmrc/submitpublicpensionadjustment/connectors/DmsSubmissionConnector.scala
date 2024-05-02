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

package uk.gov.hmrc.submitpublicpensionadjustment.connectors

import org.apache.pekko.stream.scaladsl.Source
import org.apache.pekko.util.ByteString
import play.api.http.Status.ACCEPTED
import play.api.mvc.MultipartFormData
import play.api.{Configuration, Logging}
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps, UpstreamErrorResponse}
import uk.gov.hmrc.submitpublicpensionadjustment.config.Service
import uk.gov.hmrc.submitpublicpensionadjustment.models.Done

import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.{Instant, LocalDateTime, ZoneOffset}
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DmsSubmissionConnector @Inject() (
  configuration: Configuration,
  httpClient: HttpClientV2
)(implicit ec: ExecutionContext)
    extends Logging {

  private val internalAuthToken: String = configuration.get[String]("internal-auth.token")

  private val dmsSubmission: Service = configuration.get[Service]("microservice.services.dms-submission")

  private val dmsSubmissionConfig: Configuration =
    configuration.get[Configuration]("microservice.services.dms-submission")
  private val callbackUrl: String                = dmsSubmissionConfig.get[String]("callbackUrl")
  private val source: String                     = dmsSubmissionConfig.get[String]("source")
  private val formId: String                     = dmsSubmissionConfig.get[String]("formId")
  private val businessArea: String               = dmsSubmissionConfig.get[String]("businessArea")

  def submit(
    customerId: String,
    pdf: Source[ByteString, _],
    timestamp: Instant,
    submissionReference: String,
    classificationType: String
  )(implicit hc: HeaderCarrier): Future[Done] = {

    val dateOfReceipt = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(
      LocalDateTime.ofInstant(timestamp.truncatedTo(ChronoUnit.SECONDS), ZoneOffset.UTC)
    )

    logDiagnostics(source, formId, classificationType, businessArea, callbackUrl)

    val dataParts = Seq(
      MultipartFormData.DataPart("callbackUrl", callbackUrl),
      MultipartFormData.DataPart("submissionReference", submissionReference),
      MultipartFormData.DataPart("metadata.source", source),
      MultipartFormData.DataPart("metadata.timeOfReceipt", dateOfReceipt),
      MultipartFormData.DataPart("metadata.formId", formId),
      MultipartFormData.DataPart("metadata.customerId", customerId),
      MultipartFormData.DataPart("metadata.classificationType", classificationType),
      MultipartFormData.DataPart("metadata.businessArea", businessArea)
    )

    val fileParts = Seq(
      MultipartFormData.FilePart(
        key = "form",
        filename = "final-submission.pdf",
        contentType = Some("application/pdf"),
        ref = pdf
      )
    )

    httpClient
      .post(url"$dmsSubmission/dms-submission/submit")
      .setHeader("Authorization" -> internalAuthToken)
      .withBody(
        Source(
          dataParts ++ fileParts
        )
      )
      .execute[HttpResponse]
      .flatMap { response =>
        if (response.status == ACCEPTED) {
          Future.successful(Done)
        } else {
          logger.error(
            s"DMS submission to $dmsSubmission/dms-submission/submit failed with response.status : ${response.status}"
          )
          Future.failed(
            UpstreamErrorResponse("Unexpected response during DMS submission", response.status, reportAs = 500)
          )
        }
      }

  }

  private def logDiagnostics(
    source: String,
    formId: String,
    classificationType: String,
    businessArea: String,
    callbackUrl: String
  ) =
    logger.info(
      s"source:$source, formId:$formId, classificationType : $classificationType, businessArea:$businessArea, callbackUrl:$callbackUrl"
    )
}
