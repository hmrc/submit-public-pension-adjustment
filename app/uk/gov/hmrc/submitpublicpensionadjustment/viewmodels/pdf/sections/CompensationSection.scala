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

import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.response.Period
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

  // TODO - Need to map values from final submission.
  def build(finalSubmission: FinalSubmission): Seq[CompensationSection] = Seq(
    CompensationSection(
      relatingTo = Period._2017,
      directAmount = "1",
      indirectAmount = "2",
      revisedTaxChargeTotal = "3",
      chargeYouPaid = "4",
      chargeSchemePaid = "5",
      originalSchemePaidChargeName = "originalSchemeName1",
      originalSchemePaidChargePstr = "originalSchemePstr1"
    ),
    CompensationSection(
      relatingTo = Period._2018,
      directAmount = "1",
      indirectAmount = "2",
      revisedTaxChargeTotal = "3",
      chargeYouPaid = "4",
      chargeSchemePaid = "5",
      originalSchemePaidChargeName = "originalSchemeName2",
      originalSchemePaidChargePstr = "originalSchemePstr2"
    )
  )
}
