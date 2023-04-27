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
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.{Calculation, CalculationRequest, CalculationSubmissionEvent}
import uk.gov.hmrc.submitpublicpensionadjustment.models.AuditMetadata

import java.time.{Clock, Instant}
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CalculationService @Inject()(
  dmsSubmissionService: DmsSubmissionService,
  submissionReferenceService: SubmissionReferenceService,
  auditService: AuditService,
  clock: Clock
)(implicit ec: ExecutionContext) {

  def submit(nino: String, request: CalculationRequest, auditMetadata: AuditMetadata)(implicit
                                                                                      hc: HeaderCarrier
  ): Future[String] = {

    val submissionReference: String = submissionReferenceService.random()
    val now: Instant                =  Instant.now(clock)
    val calculation: Calculation    = Calculation(nino, request.dataItem1, submissionReference, now)

    for {
      _ <- dmsSubmissionService.submitCalculation(calculation)
      _  = auditService.auditSubmitRequest(buildAudit(calculation, auditMetadata))
    } yield submissionReference
  }

  private def buildAudit(calculation: Calculation, auditMetadata: AuditMetadata): CalculationSubmissionEvent =
    CalculationSubmissionEvent(
      internalId = auditMetadata.internalId,
      affinityGroup = auditMetadata.affinityGroup,
      credentialRole = auditMetadata.credentialRole,
      calculation = calculation
    )
}
