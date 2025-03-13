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

package uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf

import uk.gov.hmrc.submitpublicpensionadjustment.exceptions.InvalidInputException
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.NewEnhancementType.{Both, InternationalEnhancement, PensionCredit}
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs._

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, ZoneId}

trait Formatting {

  def format(dob: LocalDate): String =
    DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.systemDefault()).format(dob)

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
      case Some(YearChargePaid._2022To2023) => "6 April 2022 to 5 April 2023"
      case Some(YearChargePaid._2021To2022) => "6 April 2021 to 5 April 2022"
      case Some(YearChargePaid._2020To2021) => "6 April 2020 to 5 April 2021"
      case Some(YearChargePaid._2019To2020) => "6 April 2019 to 5 April 2020"
      case Some(YearChargePaid._2018To2019) => "6 April 2018 to 5 April 2019"
      case Some(YearChargePaid._2017To2018) => "6 April 2017 to 5 April 2018"
      case Some(YearChargePaid._2016To2017) => "6 April 2016 to 5 April 2017"
      case Some(YearChargePaid._2015To2016) => "6 April 2015 to 5 April 2016"
      case _                                => NotApplicable
    }

  def formatQuarterChargePaid(quarterChargePaid: Option[QuarterChargePaid]): String =
    quarterChargePaid match {
      case Some(QuarterChargePaid.AprToJul) => "1 April to 30 June"
      case Some(QuarterChargePaid.JulToOct) => "1 July to 30 September"
      case Some(QuarterChargePaid.OctToJan) => "1 October to 31 December"
      case Some(QuarterChargePaid.JanToApr) => "1 January to 31 March"
      case _                                => NotApplicable
    }

  def formatNewExcessLifetimeAllowancePaid(
    newExcessLifetimeAllowancePaid: Option[NewExcessLifetimeAllowancePaid]
  ): String =
    newExcessLifetimeAllowancePaid match {
      case Some(NewExcessLifetimeAllowancePaid.Annualpayment) => "Annual Payment"
      case Some(NewExcessLifetimeAllowancePaid.Lumpsum)       => "Lump Sum"
      case Some(NewExcessLifetimeAllowancePaid.Both)          => "Both"
      case _                                                  => NotApplicable
    }

  def formatBoolean(optValue: Option[Boolean]): String = optValue match {
    case Some(true) => "Yes"
    case _          => "No"
  }

  def formatExcessLifetimeAllowancePaid(excessLifetimeAllowancePaid: Option[ExcessLifetimeAllowancePaid]): String =
    excessLifetimeAllowancePaid match {
      case Some(ExcessLifetimeAllowancePaid.Annualpayment) => "Annual Payment"
      case Some(ExcessLifetimeAllowancePaid.Lumpsum)       => "Lump Sum"
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

  def formatSchemeCreditConsent(optValue: Option[Boolean]): String =
    optValue match {
      case Some(true) => "Consent given"
      case None       => NotApplicable
      case _          => NotApplicable
    }

  def format(value: Boolean): String = formatBoolean(Some(value))

  def formatNumberString(input: String): String =
    if (input.forall(_.isDigit)) {
      val formattedString = input.reverse
        .grouped(3)
        .mkString(",")
        .reverse
      "£" + formattedString
    } else {
      input
    }

  def formatPoundsAmount(amount: Int): String = s"${formatNumberString(amount.toString)}"

  def formatOptPoundsAmount(optValue: Option[Int]) =
    optValue match {
      case Some(value) =>
        val formattedValue = formatNumberString(value.toString)
        s"$formattedValue"
      case None        => NotApplicable
    }

  def formatPeriodToRange(period: Period): String =
    period match {
      case Period._2016 => "2015/16"
      case Period._2017 => "2016/17"
      case Period._2018 => "2017/18"
      case Period._2019 => "2018/19"
      case Period._2020 => "2019/20"
      case Period._2021 => "2020/21"
      case Period._2022 => "2021/22"
      case Period._2023 => "2022/23"
      case _            => throw InvalidInputException(s"Invalid period while formatting period to range")
    }

  val NotEntered    = "Not Entered"
  val NotApplicable = "Not Applicable"
  val UnitedKingdom = "United Kingdom"

  val WhoPaidScheme = "Scheme"
  val WhoPaidMember = "Member"
  val WhoPaidBoth   = "Both"
  val WhoPaidNone   = "None"
}
