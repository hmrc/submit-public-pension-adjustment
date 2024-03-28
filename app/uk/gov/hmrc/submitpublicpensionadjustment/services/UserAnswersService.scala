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
import uk.gov.hmrc.submitpublicpensionadjustment.repositories.UserAnswersRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserAnswersService @Inject() (
  userAnswers: UserAnswersRepository
)(implicit ec: ExecutionContext)
    extends Logging {

  def retrieveUserAnswers(id: String): Future[Option[UserAnswers]] = userAnswers.get(id)

  def checkSubmissionStartedWithId(id: String): Future[Boolean] =
    retrieveUserAnswers(id).flatMap {
      case Some(_) =>
        Future.successful(true)
      case None    =>
        Future.successful(false)
    }
}
