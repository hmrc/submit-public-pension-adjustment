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

import play.api.libs.json._

import java.time.LocalDate

case class LifeTimeAllowance(
  benefitCrystallisationEventFlag: Boolean,
  benefitCrystallisationEventDate: LocalDate,
  changeInLifetimeAllowancePercentageInformedFlag: Boolean,
  changeInTaxCharge: ChangeInTaxCharge,
  lifetimeAllowanceProtectionOrEnhancements: LtaProtectionOrEnhancements,
  protectionType: Option[ProtectionType],
  protectionReference: Option[String],
  protectionTypeEnhancementChanged: ProtectionEnhancedChanged,
  newProtectionTypeOrEnhancement: Option[WhatNewProtectionTypeEnhancement],
  newProtectionTypeOrEnhancementReference: Option[String],
  previousLifetimeAllowanceChargeFlag: Boolean,
  previousLifetimeAllowanceChargePaymentMethod: Option[ExcessLifetimeAllowancePaid],
  previousLifetimeAllowanceChargePaidBy: Option[WhoPaidLTACharge],
  previousLifetimeAllowanceChargeSchemeNameAndTaxRef: Option[SchemeNameAndTaxRef],
  newLifetimeAllowanceChargeWillBePaidBy: Option[WhoPayingExtraLtaCharge],
  newLifetimeAllowanceChargeSchemeNameAndTaxRef: Option[LtaPensionSchemeDetails],
  newLifeTimeAllowanceAdditions: NewLifeTimeAllowanceAdditions
)
object LifeTimeAllowance {

  implicit lazy val formats: Format[LifeTimeAllowance] = Json.format
}

case class NewLifeTimeAllowanceAdditions(
  multipleBenefitCrystallisationEventFlag: Boolean,
  enhancementType: Option[EnhancementType],
  internationalEnhancementReference: Option[String],
  pensionCreditReference: Option[String],
  newEnhancementType: Option[NewEnhancementType],
  newInternationalEnhancementReference: Option[String],
  newPensionCreditReference: Option[String],
  lumpSumValue: Option[Int],
  annualPaymentValue: Option[Int],
  userSchemeDetails: Option[UserSchemeDetails],
  quarterChargePaid: Option[QuarterChargePaid],
  yearChargePaid: Option[YearChargePaid],
  newExcessLifetimeAllowancePaid: Option[NewExcessLifetimeAllowancePaid],
  newLumpSumValue: Option[Int],
  newAnnualPaymentValue: Option[Int]
)

object NewLifeTimeAllowanceAdditions {
  implicit lazy val formats: Format[NewLifeTimeAllowanceAdditions] = Json.format
}
