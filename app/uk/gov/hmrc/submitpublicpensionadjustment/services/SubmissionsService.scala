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
import uk.gov.hmrc.submitpublicpensionadjustment.models.UserAnswers
import uk.gov.hmrc.submitpublicpensionadjustment.models.submission.Submission
import uk.gov.hmrc.submitpublicpensionadjustment.repositories.{SubmissionRepository, UserAnswersRepository}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmissionsService @Inject() (
  submissions: SubmissionRepository
)(implicit ec: ExecutionContext)
    extends Logging {

  def retrieveSubmissions(id: String): Future[Option[Submission]] = submissions.get(id)

  def retrieveSubmissionsById(id: String): Future[Option[Submission]] = submissions.getByUserId(id)

  def checkSubmissionsPresentWithUniqueId(uniqueId: String): Future[Boolean] =
    retrieveSubmissions(uniqueId).flatMap {
      case Some(_) =>
        Future.successful(true)
      case None    =>
        Future.successful(false)
    }

  def checkSubmissionsPresentWithId(id: String): Future[Boolean] =
    retrieveSubmissionsById(id).flatMap {
      case Some(_) =>
        Future.successful(true)
      case None    =>
        Future.successful(false)
    }
}
