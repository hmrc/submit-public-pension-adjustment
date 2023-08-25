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

object Formatting {

  def format(dob: LocalDate): String =
    DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.systemDefault()).format(dob)

  def format(flag: Boolean): String =
    if (flag)
      "Yes"
    else
      "No"

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
  }

  def format(protectionType: ProtectionType): String = protectionType match {
    case ProtectionType.EnhancedProtection       => "Enhanced protection"
    case ProtectionType.PrimaryProtection        => "Primary protection"
    case ProtectionType.FixedProtection          => "Fixed protection"
    case ProtectionType.FixedProtection2014      => "Fixed protection 2014"
    case ProtectionType.FixedProtection2016      => "Fixed protection 2016"
    case ProtectionType.IndividualProtection2014 => "Individual protection 2014"
    case ProtectionType.IndividualProtection2016 => "Individual protection 2016"
    case ProtectionType.InternationalEnhancement => "International enhancement"
    case ProtectionType.PensionCredit            => "Pension credit"
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
      case Some(WhatNewProtectionTypeEnhancement.InternationalEnhancement) => "International enhancement"
      case Some(WhatNewProtectionTypeEnhancement.PensionCredit)            => "Pension credit"
      case _                                                               => "Not Applicable"
    }

  def formatExcessLifetimeAllowancePaid(excessLifetimeAllowancePaid: Option[ExcessLifetimeAllowancePaid]): String =
    excessLifetimeAllowancePaid match {
      case Some(ExcessLifetimeAllowancePaid.Annualpayment) => "Annual payment"
      case Some(ExcessLifetimeAllowancePaid.Lumpsum)       => "Lumpsum"
      case _                                               => "Not Applicable"
    }

  def formatWhoPaidLTACharge(whoPaidLTACharge: Option[WhoPaidLTACharge]): String =
    whoPaidLTACharge match {
      case Some(WhoPaidLTACharge.You)           => "Member"
      case Some(WhoPaidLTACharge.PensionScheme) => "Scheme"
      case _                                    => "Not Applicable"
    }

  def formatWhoPayingExtraLtaCharge(whoPayingExtraLtaCharge: Option[WhoPayingExtraLtaCharge]): String =
    whoPayingExtraLtaCharge match {
      case Some(WhoPayingExtraLtaCharge.You)           => "Member"
      case Some(WhoPayingExtraLtaCharge.PensionScheme) => "Scheme"
      case _                                           => "Not Applicable"
    }
}
