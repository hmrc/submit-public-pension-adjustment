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

import java.time.LocalDate
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.{ChangeInTaxCharge, EnhancementType, LtaProtectionOrEnhancements, NewEnhancementType, NewExcessLifetimeAllowancePaid, ProtectionEnhancedChanged, ProtectionType, QuarterChargePaid, WhatNewProtectionTypeEnhancement, YearChargePaid}
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.Formatting

class FormattingSpec extends AnyFreeSpec with Matchers {

  val formatter = new Formatting {}

  "Formatting" - {

    "format(dob: LocalDate)" - {
      "should format a given LocalDate correctly" in {
        val date = LocalDate.of(2023, 10, 3)
        formatter.format(date) shouldBe "03/10/2023"
      }
    }

    "format(changeInTaxCharge: ChangeInTaxCharge)" - {
      "should format ChangeInTaxCharge correctly" in {
        formatter.format(ChangeInTaxCharge.NewCharge)       shouldBe "New"
        formatter.format(ChangeInTaxCharge.IncreasedCharge) shouldBe "Increased"
        formatter.format(ChangeInTaxCharge.DecreasedCharge) shouldBe "Decreased"
        formatter.format(ChangeInTaxCharge.None)            shouldBe "Error"
      }
    }

    "format(ltaProtectionOrEnhancements: LtaProtectionOrEnhancements)" - {
      "should format LtaProtectionOrEnhancements correctly" in {
        formatter.format(LtaProtectionOrEnhancements.Protection)   shouldBe "Protection"
        formatter.format(LtaProtectionOrEnhancements.Enhancements) shouldBe "Enhancements"
        formatter.format(LtaProtectionOrEnhancements.Both)         shouldBe "Both"
        formatter.format(LtaProtectionOrEnhancements.No)           shouldBe "No"
      }
    }

    "format(whatNewProtectionTypeEnhancement: Option[WhatNewProtectionTypeEnhancement])" - {
      "should format WhatNewProtectionTypeEnhancement correctly" in {
        formatter.format(Some(WhatNewProtectionTypeEnhancement.EnhancedProtection))  shouldBe "Enhanced protection"
        formatter.format(Some(WhatNewProtectionTypeEnhancement.PrimaryProtection))   shouldBe "Primary protection"
        formatter.format(Some(WhatNewProtectionTypeEnhancement.FixedProtection))     shouldBe "Fixed protection"
        formatter.format(Some(WhatNewProtectionTypeEnhancement.FixedProtection2014)) shouldBe "Fixed protection 2014"
        formatter.format(Some(WhatNewProtectionTypeEnhancement.FixedProtection2016)) shouldBe "Fixed protection 2016"
        formatter.format(
          Some(WhatNewProtectionTypeEnhancement.IndividualProtection2014)
        )                                                                            shouldBe "Individual protection 2014"
        formatter.format(
          Some(WhatNewProtectionTypeEnhancement.IndividualProtection2016)
        )                                                                            shouldBe "Individual protection 2016"
        formatter.format(None)                                                       shouldBe "Not Applicable"
      }
    }

    "formatProtectionType(protectionType: Option[ProtectionType])" - {
      "should format ProtectionType correctly" in {
        formatter.formatProtectionType(Some(ProtectionType.EnhancedProtection))  shouldBe "Enhanced protection"
        formatter.formatProtectionType(Some(ProtectionType.PrimaryProtection))   shouldBe "Primary protection"
        formatter.formatProtectionType(Some(ProtectionType.FixedProtection))     shouldBe "Fixed protection"
        formatter.formatProtectionType(Some(ProtectionType.FixedProtection2014)) shouldBe "Fixed protection 2014"
        formatter.formatProtectionType(Some(ProtectionType.FixedProtection2016)) shouldBe "Fixed protection 2016"
        formatter.formatProtectionType(
          Some(ProtectionType.IndividualProtection2014)
        )                                                                        shouldBe "Individual protection 2014"
        formatter.formatProtectionType(
          Some(ProtectionType.IndividualProtection2016)
        )                                                                        shouldBe "Individual protection 2016"
        formatter.formatProtectionType(None)                                     shouldBe "Not Applicable"
      }
    }

    "formatLtaProtectionOrEnhancements(changeToProtectionType: ProtectionEnhancedChanged)" - {
      "should format ProtectionEnhancedChanged correctly" in {
        formatter.formatLtaProtectionOrEnhancements(ProtectionEnhancedChanged.Protection)  shouldBe "Protection"
        formatter.formatLtaProtectionOrEnhancements(ProtectionEnhancedChanged.Enhancement) shouldBe "Enhancements"
        formatter.formatLtaProtectionOrEnhancements(ProtectionEnhancedChanged.Both)        shouldBe "Both"
        formatter.formatLtaProtectionOrEnhancements(ProtectionEnhancedChanged.No)          shouldBe "No"
      }
    }

    "formatEnhancementType(enhancementType: Option[EnhancementType])" - {
      "should format EnhancementType correctly" in {
        formatter.formatEnhancementType(
          Some(EnhancementType.InternationalEnhancement)
        )                                                                    shouldBe "International Enhancement"
        formatter.formatEnhancementType(Some(EnhancementType.PensionCredit)) shouldBe "Pension Credit"
        formatter.formatEnhancementType(Some(EnhancementType.Both))          shouldBe "Both"
        formatter.formatEnhancementType(None)                                shouldBe "Not Applicable"
      }
    }

    "formatNewEnhancementType(newEnhancementType: Option[NewEnhancementType])" - {
      "should format NewEnhancementType correctly" in {
        formatter.formatNewEnhancementType(
          Some(NewEnhancementType.InternationalEnhancement)
        )                                                                          shouldBe "International Enhancement"
        formatter.formatNewEnhancementType(Some(NewEnhancementType.PensionCredit)) shouldBe "Pension Credit"
        formatter.formatNewEnhancementType(Some(NewEnhancementType.Both))          shouldBe "Both"
        formatter.formatNewEnhancementType(None)                                   shouldBe "Not Applicable"
      }
    }

    "formatYearChargePaid(yearChargePaid: Option[YearChargePaid])" - {
      "should format YearChargePaid correctly" in {
        formatter.formatYearChargePaid(Some(YearChargePaid._2021To2022)) shouldBe "6 April 2021 to 5 April 2022"
        formatter.formatYearChargePaid(Some(YearChargePaid._2020To2021)) shouldBe "6 April 2020 to 5 April 2021"
        formatter.formatYearChargePaid(Some(YearChargePaid._2019To2020)) shouldBe "6 April 2019 to 5 April 2020"
        formatter.formatYearChargePaid(Some(YearChargePaid._2018To2019)) shouldBe "6 April 2018 to 5 April 2019"
        formatter.formatYearChargePaid(Some(YearChargePaid._2017To2018)) shouldBe "6 April 2017 to 5 April 2018"
        formatter.formatYearChargePaid(Some(YearChargePaid._2016To2017)) shouldBe "6 April 2016 to 5 April 2017"
        formatter.formatYearChargePaid(Some(YearChargePaid._2015To2016)) shouldBe "6 April 2015 to 5 April 2016"
        formatter.formatYearChargePaid(None)                             shouldBe "Not Applicable"
      }
    }

    "formatQuarterChargePaid(quarterChargePaid: Option[QuarterChargePaid])" - {
      "should format QuarterChargePaid correctly" in {
        formatter.formatQuarterChargePaid(Some(QuarterChargePaid.AprToJul)) shouldBe "6 April to 5 July"
        formatter.formatQuarterChargePaid(Some(QuarterChargePaid.JulToOct)) shouldBe "6 July to 5 October"
        formatter.formatQuarterChargePaid(Some(QuarterChargePaid.OctToJan)) shouldBe "6 October to 5 January"
        formatter.formatQuarterChargePaid(Some(QuarterChargePaid.JanToApr)) shouldBe "6 January to 5 April"
        formatter.formatQuarterChargePaid(None)                             shouldBe "Not Applicable"
      }
    }

    "formatNewExcessLifetimeAllowancePaid(newExcessLifetimeAllowancePaid: Option[NewExcessLifetimeAllowancePaid])" - {
      "should format NewExcessLifetimeAllowancePaid correctly" in {
        formatter.formatNewExcessLifetimeAllowancePaid(
          Some(NewExcessLifetimeAllowancePaid.Annualpayment)
        )                                                                                            shouldBe "Annual Payment"
        formatter.formatNewExcessLifetimeAllowancePaid(Some(NewExcessLifetimeAllowancePaid.Lumpsum)) shouldBe "Lump Sum"
        formatter.formatNewExcessLifetimeAllowancePaid(Some(NewExcessLifetimeAllowancePaid.Both))    shouldBe "Both"
        formatter.formatNewExcessLifetimeAllowancePaid(None)                                         shouldBe "Not Applicable"
      }
    }

    "formatBoolean(optValue: Option[Boolean])" - {
      "should format optional boolean value correctly" in {
        formatter.formatBoolean(Some(true))  shouldBe "Yes"
        formatter.formatBoolean(Some(false)) shouldBe "No"
        formatter.formatBoolean(None)        shouldBe "No"
      }
    }

    "formatString(optValue: Option[String])" - {
      "should format optional string value correctly" in {
        formatter.formatString(Some("Test")) shouldBe "Test"
        formatter.formatString(Some(""))     shouldBe "Not entered"
        formatter.formatString(None)         shouldBe "Not entered"
      }
    }

    "formatStringNotApplicable(optValue: Option[String])" - {
      "should format optional string value correctly with 'Not Applicable' for empty or None" in {
        formatter.formatStringNotApplicable(Some("Test")) shouldBe "Test"
        formatter.formatStringNotApplicable(Some(""))     shouldBe "Not Applicable"
        formatter.formatStringNotApplicable(None)         shouldBe "Not Applicable"
      }
    }

    "formatPoundsAmount(amount: Int)" - {
      "should format an amount correctly with pound symbol" in {
        formatter.formatPoundsAmount(100) shouldBe "£100"
      }
    }

    "formatOptPoundsAmount(optValue: Option[Int])" - {
      "should format an optional amount correctly with pound symbol" in {
        formatter.formatOptPoundsAmount(Some(100)) shouldBe "£100"
        formatter.formatOptPoundsAmount(None)      shouldBe "Not Applicable"
      }
    }
  }
}
