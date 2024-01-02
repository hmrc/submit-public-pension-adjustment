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

package uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs

import uk.gov.hmrc.submitpublicpensionadjustment.models.{Enumerable, WithName}

sealed trait ChangeInTaxCharge

object ChangeInTaxCharge extends Enumerable.Implicits {

  case object NewCharge extends WithName(s"newCharge") with ChangeInTaxCharge
  case object IncreasedCharge extends WithName(s"increasedCharge") with ChangeInTaxCharge
  case object DecreasedCharge extends WithName(s"decreasedCharge") with ChangeInTaxCharge
  case object None extends WithName(s"none") with ChangeInTaxCharge

  val values: Seq[ChangeInTaxCharge] = Seq(
    NewCharge,
    IncreasedCharge,
    DecreasedCharge,
    None
  )

  implicit val enumerable: Enumerable[ChangeInTaxCharge] =
    Enumerable(values.map(v => v.toString -> v): _*)

}
