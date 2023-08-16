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

import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.response.{OutOfDatesTaxYearsCalculation, Period}
import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.FinalSubmission
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.Section

case class CompensationSection(
  relatingTo: Period,
  directAmount: String,
  indirectAmount: String,
  revisedTaxChargeTotal: String,
  chargeYouPaid: String,
  chargeSchemePaid: String,
  originalSchemePaidChargeName: String,
  originalSchemePaidChargePstr: String
) extends Section {

  override def orderedFieldNames(): Seq[String] = Seq(
    "directAmount",
    "indirectAmount",
    "revisedTaxChargeTotal",
    "chargeYouPaid",
    "chargeSchemePaid",
    "originalSchemePaidChargeName",
    "originalSchemePaidChargePstr"
  )

  override def period() = Some(relatingTo)
}

object CompensationSection {

  def build(finalSubmission: FinalSubmission): Seq[CompensationSection] = {
    val outOfDates = finalSubmission.calculation
      .map(_.outDates)
      .getOrElse(Seq.empty)

    outOfDates.map(buildFromOutOfDates)
  }

  private def buildFromOutOfDates(calc: OutOfDatesTaxYearsCalculation): CompensationSection =
    CompensationSection(
      relatingTo = calc.period,
      directAmount = "£" + calc.directCompensation.toString,
      indirectAmount = "£" + calc.indirectCompensation.toString,
      revisedTaxChargeTotal = "£" + calc.revisedChargableAmountAfterTaxRate.toString,
      chargeYouPaid = "£" + calc.chargePaidByMember.toString,
      chargeSchemePaid = "£" + calc.chargePaidBySchemes.toString,
      originalSchemePaidChargeName =
        calc.taxYearSchemes.headOption.map(_.name).getOrElse(""), // todo list of each scheme in a sequence
      originalSchemePaidChargePstr = calc.taxYearSchemes.headOption
        .map(_.pensionSchemeTaxReference)
        .getOrElse("") // todo list of each scheme in a sequence
    )
}
