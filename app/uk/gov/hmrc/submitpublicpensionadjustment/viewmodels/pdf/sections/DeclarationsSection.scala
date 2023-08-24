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
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.Section

case class DeclarationsSection(
  compensation: String,
  tax: String,
  trueAndComplete: String,
  onBehalfDeceased: String,
  deputyship: String
) extends Section {
  override def orderedFieldNames(): Seq[String] =
    Seq("compensation", "tax", "trueAndComplete", "onBehalfDeceased", "deputyship")
}

object DeclarationsSection {
  def build(finalSubmission: FinalSubmission): DeclarationsSection =
    DeclarationsSection(
      compensation = booleanToYesNo(finalSubmission.submissionInputs.declarations.compensation),
      tax = booleanToYesNo(finalSubmission.submissionInputs.declarations.tax),
      trueAndComplete = "Y",
      onBehalfDeceased = optionBooleanToYesNo(finalSubmission.submissionInputs.declarations.claimOnBehalfOfDeceased),
      deputyship = optionBooleanToYesNo(finalSubmission.submissionInputs.declarations.powerOfAttorney)
    )

  private def booleanToYesNo(value: Boolean): String = if (value) "Y" else "N"

  private def optionBooleanToYesNo(optValue: Option[Boolean]): String = optValue match {
    case Some(true) => "Y"
    case _          => "N"
  }
}
