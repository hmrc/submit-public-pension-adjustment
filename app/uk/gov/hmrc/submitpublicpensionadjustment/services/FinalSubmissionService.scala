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
import uk.gov.hmrc.submitpublicpensionadjustment.models._
import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.FinalSubmission
import uk.gov.hmrc.submitpublicpensionadjustment.repositories.CalcUserAnswersRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FinalSubmissionService @Inject() (
  dmsSubmissionService: DmsSubmissionService,
  queueLogicService: QueueLogicService,
  auditService: AuditService,
  calcUserAnswersRepository: CalcUserAnswersRepository
)(implicit ec: ExecutionContext)
    extends Logging {

  def submit(finalSubmission: FinalSubmission, auditMetadata: AuditMetadata)(implicit
    hc: HeaderCarrier
  ): Future[SubmissionReferences] = {

    val queueReferences: Seq[QueueReference] = queueLogicService.computeQueueReferences(finalSubmission)
    val mostSignificantQueueReference        = queueLogicService.determineMostSignificantQueueReference(queueReferences)

    val responses: Seq[Future[String]] =
      sendToDms(finalSubmission, queueReferences)

    for {
      oCalcUserAnswer <- calcUserAnswersRepository.get(auditMetadata.userId)
      refs            <- Future.sequence(responses)
    } yield {
      auditService.auditSubmitRequest(buildAudit(oCalcUserAnswer, finalSubmission, auditMetadata))
      SubmissionReferences(mostSignificantQueueReference.submissionReference, refs)
    }
  }

  private def sendToDms(
    finalSubmission: FinalSubmission,
    queueReferences: Seq[QueueReference]
  )(implicit
    hc: HeaderCarrier
  ) =
    queueReferences.map { queueReference =>
      val caseIdentifiers = CaseIdentifiers(queueReference.submissionReference, queueReferences)
      for {
        _ <-
          dmsSubmissionService.send(
            caseIdentifiers,
            finalSubmission,
            queueReference.submissionReference,
            queueReference.dmsQueue.queueName()
          )
      } yield queueReference.submissionReference
    }

  private def buildAudit(
    calcUserAnswer: Option[CalcUserAnswers],
    finalSubmission: FinalSubmission,
    auditMetadata: AuditMetadata
  ): SubmissionAuditEvent =
    SubmissionAuditEvent(
      uniqueId = calcUserAnswer.map(_.uniqueId),
      authenticated = calcUserAnswer.map(_.authenticated),
      userId = auditMetadata.userId,
      affinityGroup = auditMetadata.affinityGroup,
      credentialRole = auditMetadata.credentialRole,
      finalSubmission = finalSubmission
    )
}
