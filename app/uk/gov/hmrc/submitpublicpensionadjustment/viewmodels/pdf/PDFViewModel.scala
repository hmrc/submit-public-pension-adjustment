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

package uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf

import play.api.i18n.Messages
import uk.gov.hmrc.submitpublicpensionadjustment.models.CaseIdentifiers
import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.FinalSubmission
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.sections._

case class PDFViewModel(
  caseNumber: String,
  caseIdentificationSection: CaseIdentificationSection,
  administrativeDetailsSection: AdministrativeDetailsSection,
  onBehalfOfSection: Option[OnBehalfOfSection],
  lifetimeAllowanceSection: Option[LifetimeAllowanceSection],
  publicSectorSchemeDetailsSections: Seq[PublicSectorSchemeDetailsSection],
  compensationSections: Seq[CompensationSection],
  incomeSubJourneySection: IncomeSubJourneySection,
  taxAdministrationFrameworkSections: Seq[TaxAdministrationFrameworkSection],
  additionalOrHigherReliefSection: Option[AdditionalOrHigherReliefSection],
  paymentInformationSection: Option[PaymentInformationSection],
  declarationsSection: DeclarationsSection
) {

  def prettyPrint(messages: Messages): String = displayLines(messages).mkString("", "\n", "")

  private def displayLines(messages: Messages): Seq[String] =
    Seq("Calculate your public service pension adjustment") ++
      administrativeDetailsSection.displayLines(messages) ++
      optionalDisplayLines(messages, onBehalfOfSection) ++
      optionalDisplayLines(messages, lifetimeAllowanceSection) ++
      publicSectorSchemeDetailsSections.flatMap(section => optionalDisplayLines(messages, Some(section))) ++
      compensationSections.flatMap(section => optionalDisplayLines(messages, Some(section))) ++
      incomeSubJourneySection.displayLines(messages) ++
      taxAdministrationFrameworkSections.flatMap(section => optionalDisplayLines(messages, Some(section))) ++
      optionalDisplayLines(messages, additionalOrHigherReliefSection) ++
      optionalDisplayLines(messages, paymentInformationSection) ++
      declarationsSection.displayLines(messages)

  private def optionalDisplayLines(messages: Messages, optionalSection: Option[Section]): Seq[String] =
    optionalSection match {
      case Some(section) => section.displayLines(messages)
      case None          => Seq()
    }
}

object PDFViewModel {
  def build(caseIdentifiers: CaseIdentifiers, finalSubmission: FinalSubmission): PDFViewModel =
    PDFViewModel(
      caseIdentifiers.caseNumber,
      CaseIdentificationSection.build(caseIdentifiers),
      AdministrativeDetailsSection.build(finalSubmission),
      OnBehalfOfSection.build(finalSubmission),
      LifetimeAllowanceSection.build(finalSubmission),
      PublicSectorSchemeDetailsSection.build(finalSubmission),
      CompensationSection.build(finalSubmission),
      IncomeSubJourneySection.build(finalSubmission),
      TaxAdministrationFrameworkSection.build(finalSubmission),
      AdditionalOrHigherReliefSection.build(finalSubmission),
      PaymentInformationSection.build(finalSubmission),
      DeclarationsSection.build(finalSubmission)
    )
}
