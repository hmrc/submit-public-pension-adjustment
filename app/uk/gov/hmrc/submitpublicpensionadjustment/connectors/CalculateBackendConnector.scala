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

import com.google.inject.Inject
import play.api.Logging
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps, UpstreamErrorResponse}
import uk.gov.hmrc.submitpublicpensionadjustment.config.AppConfig
import uk.gov.hmrc.submitpublicpensionadjustment.models.submission.RetrieveSubmissionResponse
import uk.gov.hmrc.submitpublicpensionadjustment.models.{CalcUserAnswers, RetrieveSubmissionInfo, UniqueId}

import scala.concurrent.{ExecutionContext, Future}

class CalculateBackendConnector @Inject() (
  config: AppConfig,
  httpClient2: HttpClientV2
)(implicit
  ec: ExecutionContext
) extends Logging {

  def retrieveSubmission(
    retrieveSubmissionInfo: RetrieveSubmissionInfo
  )(implicit hc: HeaderCarrier): Future[RetrieveSubmissionResponse] =
    httpClient2
      .post(
        url"${config.cppaBaseUrl}/calculate-public-pension-adjustment/retrieve-submission"
      )
      .withBody(Json.toJson(retrieveSubmissionInfo))
      .execute
      .flatMap { response =>
        response.status match {
          case OK =>
            Future.successful(response.json.as[RetrieveSubmissionResponse])
          case _  =>
            updateSubmissionFlag(retrieveSubmissionInfo.submissionUniqueId)
            logger.error(
              s"Unexpected response from /calculate-public-pension-adjustment/submission/${retrieveSubmissionInfo.submissionUniqueId.value} with status : ${response.status}"
            )
            Future.failed(
              UpstreamErrorResponse(
                "Unexpected response from /calculate-public-pension-adjustment/submission/submissionUniqueId",
                response.status
              )
            )
        }
      }
      .recoverWith { _ =>
        updateSubmissionFlag(retrieveSubmissionInfo.submissionUniqueId)
        logger.error(
          s"Future failed for an API call /calculate-public-pension-adjustment/submission/${retrieveSubmissionInfo.submissionUniqueId.value}"
        )
        Future.failed(
          UpstreamErrorResponse(
            "Future failed for an API call /calculate-public-pension-adjustment/submission/submissionUniqueId",
            INTERNAL_SERVER_ERROR
          )
        )
      }

  def retrieveCalcUserAnswers(
    retrieveSubmissionInfo: RetrieveSubmissionInfo
  )(implicit hc: HeaderCarrier): Future[CalcUserAnswers] =
    httpClient2
      .post(
        url"${config.cppaBaseUrl}/calculate-public-pension-adjustment/retrieve-user-answers"
      )
      .withBody(Json.toJson(retrieveSubmissionInfo))
      .execute
      .flatMap { response =>
        response.status match {
          case OK =>
            Future.successful(response.json.as[CalcUserAnswers])
          case _  =>
            logger.error(
              s"Unexpected response from /calculate-public-pension-adjustment/retrieve-user-answers/${retrieveSubmissionInfo.submissionUniqueId.value} with status : ${response.status}"
            )
            Future.failed(
              UpstreamErrorResponse(
                "Unexpected response from /calculate-public-pension-adjustment/retrieve-user-answers",
                response.status
              )
            )
        }
      }

  def updateSubmissionFlag(
    id: UniqueId
  )(implicit hc: HeaderCarrier): Future[Boolean] =
    httpClient2
      .get(url"${config.cppaBaseUrl}/calculate-public-pension-adjustment/submission-status-update/${id.value}")
      .execute
      .map { response =>
        response.status match {
          case OK =>
            true
          case _  =>
            logger.error(
              s"Unexpected response from /calculate-public-pension-adjustment/submission-status-update/${id.value} with status : ${response.status}"
            )
            UpstreamErrorResponse(
              "Unexpected response from /calculate-public-pension-adjustment/submission-status-update",
              response.status
            )
            false
        }
      }
}
