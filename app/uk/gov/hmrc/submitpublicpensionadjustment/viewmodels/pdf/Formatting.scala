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

import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.{ChangeInTaxCharge, ExcessLifetimeAllowancePaid, LtaProtectionOrEnhancements, ProtectionType, WhatNewProtectionTypeEnhancement, WhoPaidLTACharge, WhoPayingExtraLtaCharge}

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

  def format(protectionType: ProtectionType): String = protectionType match {
    case ProtectionType.EnhancedProtection       => "Enhanced protection"
    case ProtectionType.PrimaryProtection        => "Primary protection"
    case ProtectionType.FixedProtection          => "Fixed protection"
    case ProtectionType.FixedProtection2014      => "Fixed protection 2014"
    case ProtectionType.FixedProtection2016      => "Fixed protection 2016"
    case ProtectionType.IndividualProtection2014 => "Individual protection 2014"
    case ProtectionType.IndividualProtection2016 => "Individual protection 2016"
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

  def format(value: Boolean): String = formatBoolean(Some(value))

  def formatPoundsAmount(amount: Int): String = s"£${amount.toString}"

  val NotEntered    = "Not entered"
  val NotApplicable = "Not Applicable"
  val UnitedKingdom = "United Kingdom"

  val WhoPaidScheme = "Scheme"
  val WhoPaidMember = "Member"
  val WhoPaidBoth   = "Both"
  val WhoPaidNone   = "None"
}
