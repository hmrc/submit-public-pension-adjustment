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

package uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.response

import play.api.libs.json.{Reads, Writes, __}

case class CalculationResponse(
  resubmission: Resubmission,
  totalAmounts: TotalAmounts,
  outDates: List[OutOfDatesTaxYearsCalculation],
  inDates: List[InDatesTaxYearsCalculation]
)

object CalculationResponse {

  implicit lazy val reads: Reads[CalculationResponse] = {

    import play.api.libs.functional.syntax._

    ((__ \ "resubmission").read[Resubmission] and
      (__ \ "totalAmounts").read[TotalAmounts] and
      (__ \ "outDates").read[List[OutOfDatesTaxYearsCalculation]] and
      (__ \ "inDates").read[List[InDatesTaxYearsCalculation]])(CalculationResponse(_, _, _, _))
  }

  implicit lazy val writes: Writes[CalculationResponse] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "resubmission").write[Resubmission] and
        (__ \ "totalAmounts").write[TotalAmounts] and
        (__ \ "outDates").write[List[OutOfDatesTaxYearsCalculation]] and
        (__ \ "inDates").write[List[InDatesTaxYearsCalculation]]
    )(a =>
      (
        a.resubmission,
        a.totalAmounts,
        a.outDates,
        a.inDates
      )
    )
  }
}
