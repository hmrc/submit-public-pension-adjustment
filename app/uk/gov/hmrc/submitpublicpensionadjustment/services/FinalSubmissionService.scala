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

import play.api.Logging
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.submitpublicpensionadjustment.models._
import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.FinalSubmission

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FinalSubmissionService @Inject() (
  dmsSubmissionService: DmsSubmissionService,
  queueLogicService: QueueLogicService,
  auditService: AuditService
)(implicit ec: ExecutionContext)
    extends Logging {

  def submit(finalSubmission: FinalSubmission, auditMetadata: AuditMetadata)(implicit
    hc: HeaderCarrier
  ): Future[SubmissionReferences] = {

    val queueReferences: Seq[QueueReference] = queueLogicService.computeQueueReferences(finalSubmission)
    val mostSignificantQueueReference        = queueLogicService.determineMostSignificantQueueReference(queueReferences)
    logger.info(s"queueReferences : $queueReferences - mostSignificantQueueReference : $mostSignificantQueueReference")

    val responses: Seq[Future[String]] =
      sendToDms(finalSubmission, auditMetadata, queueReferences)

    val allSubmissionReferences: Future[Seq[String]] = Future.sequence(responses)
    allSubmissionReferences.map(refs => SubmissionReferences(mostSignificantQueueReference.submissionReference, refs))
  }

  private def sendToDms(
    finalSubmission: FinalSubmission,
    auditMetadata: AuditMetadata,
    queueReferences: Seq[QueueReference]
  )(implicit
    hc: HeaderCarrier
  ) = {
    val responses: Seq[Future[String]] = queueReferences.map { queueReference =>
      val caseIdentifiers = CaseIdentifiers(queueReference.submissionReference, queueReferences)
      for {
        _ <-
          dmsSubmissionService.send(
            caseIdentifiers,
            finalSubmission,
            queueReference.submissionReference,
            queueReference.dmsQueue.queueName()
          )
        _  = auditService.auditSubmitRequest(buildAudit(finalSubmission, auditMetadata))
      } yield queueReference.submissionReference
    }
    responses
  }

  private def buildAudit(finalSubmission: FinalSubmission, auditMetadata: AuditMetadata): FinalSubmissionEvent =
    FinalSubmissionEvent(
      internalId = auditMetadata.internalId,
      affinityGroup = auditMetadata.affinityGroup,
      credentialRole = auditMetadata.credentialRole,
      finalSubmission = finalSubmission
    )
}
