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

import play.api.libs.json._

sealed trait Income

object Income {

  case object BelowThreshold extends Income
  case class AboveThreshold(adjustedIncome: Int) extends Income

  implicit lazy val reads: Reads[Income] =
    (__ \ "incomeAboveThreshold").read[Boolean].flatMap {
      case true  =>
        (__ \ "adjustedIncome").read[Int].map(Income.AboveThreshold)
      case false =>
        Reads(_ => JsSuccess(Income.BelowThreshold))
    }

  implicit lazy val writes: Writes[Income] = Writes {
    case Income.BelowThreshold                 =>
      Json.obj(
        "incomeAboveThreshold" -> false
      )
    case Income.AboveThreshold(adjustedIncome) =>
      Json.obj(
        "incomeAboveThreshold" -> true,
        "adjustedIncome"       -> adjustedIncome
      )
  }
}
