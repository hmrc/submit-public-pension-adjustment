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

sealed trait MaybePIAUnchangedOrDecreased

object MaybePIAUnchangedOrDecreased extends Enumerable.Implicits {

  case object Yes extends WithName("yes") with MaybePIAUnchangedOrDecreased
  case object No extends WithName("no") with MaybePIAUnchangedOrDecreased
  case object IDoNotKnow extends WithName("idk") with MaybePIAUnchangedOrDecreased

  val values: Seq[MaybePIAUnchangedOrDecreased] = Seq(
    Yes,
    No,
    IDoNotKnow
  )

  implicit val enumerable: Enumerable[MaybePIAUnchangedOrDecreased] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
