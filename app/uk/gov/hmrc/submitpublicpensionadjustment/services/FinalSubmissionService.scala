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

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.FinalSubmission
import uk.gov.hmrc.submitpublicpensionadjustment.models.{AuditMetadata, FinalSubmissionEvent}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FinalSubmissionService @Inject() (
  dmsSubmissionService: DmsSubmissionService,
  submissionReferenceService: SubmissionReferenceService,
  auditService: AuditService
)(implicit ec: ExecutionContext) {

  def submit(finalSubmission: FinalSubmission, auditMetadata: AuditMetadata)(implicit
    hc: HeaderCarrier
  ): Future[String] = {

    val submissionReference: String = submissionReferenceService.random()

    for {
      _ <- dmsSubmissionService.send(finalSubmission.submissionInputs.caseNumber, finalSubmission, submissionReference)
      _  = auditService.auditSubmitRequest(buildAudit(finalSubmission, auditMetadata))
    } yield submissionReference
  }

  private def buildAudit(finalSubmission: FinalSubmission, auditMetadata: AuditMetadata): FinalSubmissionEvent =
    FinalSubmissionEvent(
      internalId = auditMetadata.internalId,
      affinityGroup = auditMetadata.affinityGroup,
      credentialRole = auditMetadata.credentialRole,
      finalSubmission = finalSubmission
    )
}
