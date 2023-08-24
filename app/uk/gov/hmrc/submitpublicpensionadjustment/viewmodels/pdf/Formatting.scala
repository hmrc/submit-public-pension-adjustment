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

package uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf

import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.ChangeInTaxCharge

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, ZoneId}

object Formatting {

  def format(dob: LocalDate) =
    DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.systemDefault()).format(dob)

  def format(changeInTaxCharge: ChangeInTaxCharge) = changeInTaxCharge match {
    case ChangeInTaxCharge.NewCharge       => "New"
    case ChangeInTaxCharge.IncreasedCharge => "Increased"
    case ChangeInTaxCharge.DecreasedCharge => "Decreased"
    case ChangeInTaxCharge.None            => "Error"
  }
}
