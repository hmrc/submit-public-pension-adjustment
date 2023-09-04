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
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.submitpublicpensionadjustment.config.AppConfig
import uk.gov.hmrc.submitpublicpensionadjustment.models.SubmissionAuditEvent

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AuditService @Inject() (
  auditConnector: AuditConnector,
  appConfig: AppConfig
)(implicit ec: ExecutionContext) {

  def auditSubmitRequest(event: SubmissionAuditEvent)(implicit hc: HeaderCarrier): Unit =
    auditConnector.sendExplicitAudit(appConfig.submissionAuditEventName, event)
}
