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

package uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs
import uk.gov.hmrc.submitpublicpensionadjustment.models.{Enumerable, WithName}

sealed trait EnhancementType

object EnhancementType extends Enumerable.Implicits {

  case object InternationalEnhancement extends WithName("internationalEnhancement") with EnhancementType
  case object PensionCredit extends WithName("pensionCredit") with EnhancementType
  case object Both extends WithName("both") with EnhancementType

  val values: Seq[EnhancementType] = Seq(
    InternationalEnhancement,
    PensionCredit,
    Both
  )

  implicit val enumerable: Enumerable[EnhancementType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
