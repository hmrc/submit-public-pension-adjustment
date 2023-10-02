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

import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.NewEnhancementType.{Both, InternationalEnhancement, PensionCredit}
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.{ChangeInTaxCharge, EnhancementType, ExcessLifetimeAllowancePaid, LtaProtectionOrEnhancements, NewEnhancementType, NewExcessLifetimeAllowancePaid, ProtectionEnhancedChanged, ProtectionType, QuarterChargePaid, WhatNewProtectionTypeEnhancement, WhoPaidLTACharge, WhoPayingExtraLtaCharge, YearChargePaid}

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, ZoneId}

trait Formatting {

  def format(dob: LocalDate): String =
    DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.systemDefault()).format(dob)

  def format(changeInTaxCharge: ChangeInTaxCharge): String = changeInTaxCharge match {
    case ChangeInTaxCharge.NewCharge       => "New"
    case ChangeInTaxCharge.IncreasedCharge => "Increased"
    case ChangeInTaxCharge.DecreasedCharge => "Decreased"
    case ChangeInTaxCharge.None            => "Error"
  }

  def format(ltaProtectionOrEnhancements: LtaProtectionOrEnhancements): String = ltaProtectionOrEnhancements match {
    case LtaProtectionOrEnhancements.Protection   => "Protection"
    case LtaProtectionOrEnhancements.Enhancements => "Enhancements"
    case LtaProtectionOrEnhancements.Both         => "Both"
    case LtaProtectionOrEnhancements.No           => "No"
  }

  def format(whatNewProtectionTypeEnhancement: Option[WhatNewProtectionTypeEnhancement]): String =
    whatNewProtectionTypeEnhancement match {
      case Some(WhatNewProtectionTypeEnhancement.EnhancedProtection)       => "Enhanced protection"
      case Some(WhatNewProtectionTypeEnhancement.PrimaryProtection)        => "Primary protection"
      case Some(WhatNewProtectionTypeEnhancement.FixedProtection)          => "Fixed protection"
      case Some(WhatNewProtectionTypeEnhancement.FixedProtection2014)      => "Fixed protection 2014"
      case Some(WhatNewProtectionTypeEnhancement.FixedProtection2016)      => "Fixed protection 2016"
      case Some(WhatNewProtectionTypeEnhancement.IndividualProtection2014) => "Individual protection 2014"
      case Some(WhatNewProtectionTypeEnhancement.IndividualProtection2016) => "Individual protection 2016"
      case _                                                               => NotApplicable
    }

  def formatProtectionType(protectionType: Option[ProtectionType]): String = protectionType match {
    case Some(ProtectionType.EnhancedProtection)       => "Enhanced protection"
    case Some(ProtectionType.PrimaryProtection)        => "Primary protection"
    case Some(ProtectionType.FixedProtection)          => "Fixed protection"
    case Some(ProtectionType.FixedProtection2014)      => "Fixed protection 2014"
    case Some(ProtectionType.FixedProtection2016)      => "Fixed protection 2016"
    case Some(ProtectionType.IndividualProtection2014) => "Individual protection 2014"
    case Some(ProtectionType.IndividualProtection2016) => "Individual protection 2016"
    case _                                             => NotApplicable
  }

  def formatLtaProtectionOrEnhancements(changeToProtectionType: ProtectionEnhancedChanged): String =
    changeToProtectionType match {
      case ProtectionEnhancedChanged.Protection  => "Protection"
      case ProtectionEnhancedChanged.Enhancement => "Enhancements"
      case ProtectionEnhancedChanged.Both        => "Both"
      case ProtectionEnhancedChanged.No          => "No"
      case _                                     => NotApplicable
    }

  def formatEnhancementType(enhancementType: Option[EnhancementType]): String =
    enhancementType match {
      case Some(EnhancementType.InternationalEnhancement) => "International Enhancement"
      case Some(EnhancementType.PensionCredit)            => "Pension Credit"
      case Some(EnhancementType.Both)                     => "Both"
      case _                                              => NotApplicable
    }

  def formatNewEnhancementType(newEnhancementType: Option[NewEnhancementType]): String =
    newEnhancementType match {
      case Some(InternationalEnhancement) => "International Enhancement"
      case Some(PensionCredit)            => "Pension Credit"
      case Some(Both)                     => "Both"
      case _                              => NotApplicable
    }

  def formatYearChargePaid(yearChargePaid: Option[YearChargePaid]): String =
    yearChargePaid match {
      case Some(_2021To2022) => "6 April 2021 to 5 April 2022"
      case Some(_2020To2021) => "6 April 2020 to 5 April 2021"
      case Some(_2019To2020) => "6 April 2019 to 5 April 2020"
      case Some(_2018To2019) => "6 April 2018 to 5 April 2019"
      case Some(_2017To2018) => "6 April 2017 to 5 April 2018"
      case Some(_2016To2017) => "6 April 2016 to 5 April 2017"
      case Some(_2015To2016) => "6 April 2015 to 5 April 2016"
      case _                 => NotApplicable
    }

  def formatQuarterChargePaid(quarterChargePaid: Option[QuarterChargePaid]): String =
    quarterChargePaid match {
      case Some(aprToJul) => "6 April to 5 July"
      case Some(julToOct) => "6 July to 5 October"
      case Some(octToJan) => "6 October to 5 January"
      case Some(janToApr) => "6 January to 5 April"
      case _              => NotApplicable
    }

  def formatNewExcessLifetimeAllowancePaid(
    newExcessLifetimeAllowancePaid: Option[NewExcessLifetimeAllowancePaid]
  ): String =
    newExcessLifetimeAllowancePaid match {
      case Some(annualPayment) => "Annual Payment"
      case Some(lumpSum)       => "Lump Sum"
      case Some(both)          => "Both"
      case _                   => NotApplicable
    }

  def formatBoolean(optValue: Option[Boolean]): String = optValue match {
    case Some(true) => "Yes"
    case _          => "No"
  }

  def formatExcessLifetimeAllowancePaid(excessLifetimeAllowancePaid: Option[ExcessLifetimeAllowancePaid]): String =
    excessLifetimeAllowancePaid match {
      case Some(ExcessLifetimeAllowancePaid.Annualpayment) => "Annual payment"
      case Some(ExcessLifetimeAllowancePaid.Lumpsum)       => "Lumpsum"
      case Some(ExcessLifetimeAllowancePaid.Both)          => "Both"
      case _                                               => NotApplicable
    }

  def formatWhoPaidLTACharge(whoPaidLTACharge: Option[WhoPaidLTACharge]): String =
    whoPaidLTACharge match {
      case Some(WhoPaidLTACharge.You)           => "Member"
      case Some(WhoPaidLTACharge.PensionScheme) => "Scheme"
      case _                                    => NotApplicable
    }

  def formatWhoPayingExtraLtaCharge(whoPayingExtraLtaCharge: Option[WhoPayingExtraLtaCharge]): String =
    whoPayingExtraLtaCharge match {
      case Some(WhoPayingExtraLtaCharge.You)           => "Member"
      case Some(WhoPayingExtraLtaCharge.PensionScheme) => "Scheme"
      case _                                           => NotApplicable
    }

  def formatString(optValue: Option[String]) =
    optValue match {
      case Some("")    => NotEntered
      case Some(value) => value
      case None        => NotEntered
    }

  def formatStringNotApplicable(optValue: Option[String]) =
    optValue match {
      case Some("")    => NotApplicable
      case Some(value) => value
      case None        => NotApplicable
    }

  def format(value: Boolean): String = formatBoolean(Some(value))

  def formatPoundsAmount(amount: Int): String = s"£${amount.toString}"

  def formatOptPoundsAmount(optValue: Option[Int]) =
    optValue match {
      case Some(value) => s"£${value.toString}"
      case None        => NotApplicable
    }

  val NotEntered    = "Not entered"
  val NotApplicable = "Not Applicable"
  val UnitedKingdom = "United Kingdom"

  val WhoPaidScheme = "Scheme"
  val WhoPaidMember = "Member"
  val WhoPaidBoth   = "Both"
  val WhoPaidNone   = "None"
}
