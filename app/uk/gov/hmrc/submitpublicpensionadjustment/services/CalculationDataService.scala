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

package uk.gov.hmrc.submitpublicpensionadjustment.services

import play.api.Logging
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.submitpublicpensionadjustment.connectors.CalculateBackendConnector
import uk.gov.hmrc.submitpublicpensionadjustment.models.submission.Submission
import uk.gov.hmrc.submitpublicpensionadjustment.models.{RetrieveSubmissionInfo, UniqueId}
import uk.gov.hmrc.submitpublicpensionadjustment.repositories.{CalcUserAnswersRepository, SubmissionRepository}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class CalculationDataService @Inject() (
  calculateBackendConnector: CalculateBackendConnector,
  submissionRepository: SubmissionRepository,
  userAnswersService: UserAnswersService,
  calcUserAnswersRepository: CalcUserAnswersRepository
) extends Logging {

  def retrieveSubmission(
    userId: String,
    submissionUniqueId: String
  )(implicit executionContext: ExecutionContext, hc: HeaderCarrier): Future[Boolean] =
    calculateBackendConnector
      .retrieveSubmission(RetrieveSubmissionInfo(userId, UniqueId(submissionUniqueId)))
      .transformWith {
        case Success(submissionResponse) =>
          for {
            _ <- userAnswersService.clearById(userId)
            r <- submissionRepository
                   .insert(
                     Submission(
                       userId,
                       userId,
                       submissionUniqueId,
                       submissionResponse.calculationInputs,
                       submissionResponse.calculation
                     )
                   )
                   .transformWith {
                     case Failure(exception) =>
                       logger.error(
                         s"Insert into submissionRepository for submissionUniqueId : $submissionUniqueId - failed with message : $exception"
                       )
                       Future(false)
                     case Success(_)         => Future(true)
                   }
          } yield r

        case Failure(exception) =>
          logger.error(
            s"Could not retrieve submission from calculate-public-pension-adjustment backend for submissionUniqueId : $submissionUniqueId - failed with message : $exception"
          )
          Future(false)
      }

  def retrieveCalcUserAnswers(
    internalId: String,
    submissionUniqueId: String
  )(implicit executionContext: ExecutionContext, hc: HeaderCarrier): Future[Boolean] =
    calculateBackendConnector
      .retrieveCalcUserAnswers(RetrieveSubmissionInfo(internalId, UniqueId(submissionUniqueId)))
      .transformWith {
        case Success(calcUserAnswers) =>
          calcUserAnswersRepository
            .set(calcUserAnswers)
            .transformWith {
              case Failure(exception) =>
                logger.error(
                  s"Insert into CalcUserAnswersRepository for submissionUniqueId : $submissionUniqueId - failed with message : $exception"
                )
                Future(false)
              case Success(_)         => Future(true)
            }

        case Failure(exception) =>
          logger.error(
            s"Could not retrieve CalcUserAnswers from calculate-public-pension-adjustment backend for submissionUniqueId : $submissionUniqueId - failed with message : $exception"
          )
          Future(false)
      }
}
