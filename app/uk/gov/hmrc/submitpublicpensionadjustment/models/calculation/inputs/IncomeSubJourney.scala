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

import play.api.libs.json.Json

case class IncomeSubJourney(
  salarySacrificeAmount: Option[Int],
  flexibleRemunerationAmount: Option[Int],
  rASContributionsAmount: Option[Int],
  lumpsumDeathBenefitsAmount: Option[Int],
  isAboveThreshold: Option[Boolean],
  taxReliefAmount: Option[Int],
  adjustedIncomeAmount: Option[Int],
  taxReliefPensionAmount: Option[Int],
  personalContributionsAmount: Option[Int],
  reliefClaimedOnOverseasPensionsAmount: Option[Int],
  giftAidAmount: Option[Int],
  personalAllowanceAmount: Option[Int],
  tradeUnionOrPoliceReliefAmount: Option[Int],
  blindPersonsAllowanceAmount: Option[Int],
  thresholdIncomeAmount: Option[Int]
)

object IncomeSubJourney {
  implicit val format = Json.format[IncomeSubJourney]
}
