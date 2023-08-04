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

package uk.gov.hmrc.submitpublicpensionadjustment.services

import play.api.Configuration
import uk.gov.hmrc.submitpublicpensionadjustment.models._
import uk.gov.hmrc.submitpublicpensionadjustment.models.dms.{Compensation, CompensationAmendment, DmsQueue, LTA, MiniRegime, MiniRegimeAmendment}
import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.FinalSubmission
import uk.gov.hmrc.submitpublicpensionadjustment.services.QueueLogicService.base64Decode

import java.util.Base64
import javax.inject.{Inject, Singleton}

@Singleton
class QueueLogicService @Inject() (
  configuration: Configuration,
  submissionReferenceService: SubmissionReferenceService
) {

  def computeQueueReferences(finalSubmission: FinalSubmission): Seq[QueueReference] =
    AllPossibleQueues
      .filter(dmsQueue => dmsQueue.isRequired(finalSubmission))
      .map(requiredDmsQueue => QueueReference(requiredDmsQueue, submissionReferenceService.random()))

  private val AllPossibleQueues = Seq[DmsQueue](
    Compensation(queueName("compensationQueueBase64")),
    CompensationAmendment(queueName("compensationAmendmentQueueBase64")),
    MiniRegime(queueName("miniRegimeQueueBase64")),
    MiniRegimeAmendment(queueName("miniRegimeAmendmentQueueBase64")),
    LTA(queueName("ltaQueueBase64"))
  )

  private def queueName(configKey: String) = base64Decode(
    configuration.get[Configuration]("microservice.services.dms-submission").get[String](configKey)
  )
}

object QueueLogicService {
  def base64Decode(base64: String) = new String(Base64.getDecoder.decode(base64))
}
