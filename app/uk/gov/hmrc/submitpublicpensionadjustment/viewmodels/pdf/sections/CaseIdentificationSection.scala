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

package uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.sections

import uk.gov.hmrc.submitpublicpensionadjustment.models.CaseIdentifiers
import uk.gov.hmrc.submitpublicpensionadjustment.models.dms._
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.Section

case class CaseIdentificationSection(
  compensation: String,
  compensationAmendment: String,
  miniRegime: String,
  miniRegimeAmendment: String,
  lta: String
) extends Section {

  override def orderedFieldNames(): Seq[String] =
    Seq("compensation", "compensationAmendment", "miniRegime", "miniRegimeAmendment", "lta")

}

object CaseIdentificationSection {

  def build(caseIdentifiers: CaseIdentifiers): CaseIdentificationSection =
    CaseIdentificationSection(
      caseIdentifiers.queueReferences
        .find(qr => qr.dmsQueue.isInstanceOf[Compensation])
        .map(qr => qr.submissionReference).getOrElse("Not Applicable"),
      caseIdentifiers.queueReferences
        .find(qr => qr.dmsQueue.isInstanceOf[CompensationAmendment])
        .map(qr => qr.submissionReference).getOrElse("Not Applicable"),
      caseIdentifiers.queueReferences
        .find(qr => qr.dmsQueue.isInstanceOf[MiniRegime])
        .map(qr => qr.submissionReference).getOrElse("Not Applicable"),
      caseIdentifiers.queueReferences
        .find(qr => qr.dmsQueue.isInstanceOf[MiniRegimeAmendment])
        .map(qr => qr.submissionReference).getOrElse("Not Applicable"),
      caseIdentifiers.queueReferences.find(qr => qr.dmsQueue.isInstanceOf[LTA]).map(qr => qr.submissionReference).getOrElse("Not Applicable")
    )
}
