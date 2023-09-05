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

import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.FinalSubmission
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.{Formatting, Section}

case class PublicSectorSchemeDetailsSection(
  schemeName: String,
  pstr: String,
  reformReference: String,
  legacyReference: String
) extends Section {
  override def orderedFieldNames(): Seq[String] = Seq("schemeName", "pstr", "reformReference", "legacyReference")
}

object PublicSectorSchemeDetailsSection extends Formatting {

  def build(finalSubmission: FinalSubmission): Seq[PublicSectorSchemeDetailsSection] =
    finalSubmission.submissionInputs.calculationInputSchemeIdentifiers.map { schemeIdentifier =>
      PublicSectorSchemeDetailsSection(
        schemeName = schemeIdentifier.relatedToScheme.schemeName,
        pstr = schemeIdentifier.relatedToScheme.pstr.value,
        reformReference = formatString(schemeIdentifier.reformReference),
        legacyReference = formatString(schemeIdentifier.legacyReference)
      )
    }
}
