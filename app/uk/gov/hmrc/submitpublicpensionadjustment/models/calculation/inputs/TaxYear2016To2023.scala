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
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.response.TaxYearScheme

import java.time.LocalDate
import scala.math.Ordered.orderingToOrdered

sealed trait TaxYear2016To2023 extends TaxYear

object TaxYear2016To2023 {

  case class NormalTaxYear(
    pensionInputAmount: Int,
    taxYearSchemes: List[TaxYearScheme],
    totalIncome: Int,
    chargePaidByMember: Int,
    period: Period,
    income: Option[Income] = None,
    pensionInput2016PostAmount: Option[Int] = None
  ) extends TaxYear2016To2023

  case class InitialFlexiblyAccessedTaxYear(
    definedBenefitInputAmount: Int,
    flexiAccessDate: LocalDate,
    preAccessDefinedContributionInputAmount: Int,
    postAccessDefinedContributionInputAmount: Int,
    taxYearSchemes: List[TaxYearScheme],
    totalIncome: Int,
    chargePaidByMember: Int,
    period: Period,
    income: Option[Income] = None,
    definedBenefitInput2016PostAmount: Option[Int] = None,
    definedContributionInput2016PostAmount: Option[Int] = None,
    postAccessDefinedContributionInput2016PostAmount: Option[Int] = None
  ) extends TaxYear2016To2023

  case class PostFlexiblyAccessedTaxYear(
    definedBenefitInputAmount: Int,
    definedContributionInputAmount: Int,
    totalIncome: Int,
    chargePaidByMember: Int,
    taxYearSchemes: List[TaxYearScheme],
    period: Period,
    income: Option[Income] = None,
    definedBenefitInput2016PostAmount: Option[Int] = None,
    definedContributionInput2016PostAmount: Option[Int] = None
  ) extends TaxYear2016To2023

  implicit lazy val reads: Reads[TaxYear2016To2023] = {

    import play.api.libs.functional.syntax._

    val normalReads: Reads[TaxYear2016To2023] = ((__ \ "pensionInputAmount").read[Int] and
      (__ \ "taxYearSchemes").read[List[TaxYearScheme]] and
      (__ \ "totalIncome").read[Int] and
      (__ \ "chargePaidByMember").read[Int] and
      (__ \ "period").read[Period] and
      (__ \ "income").readNullable[Income] and
      (__ \ "pensionInput2016PostAmount").readNullable[Int])(
      TaxYear2016To2023.NormalTaxYear
    )

    val initialReads: Reads[TaxYear2016To2023] = ((__ \ "definedBenefitInputAmount").read[Int] and
      (__ \ "flexiAccessDate").read[LocalDate] and
      (__ \ "preAccessDefinedContributionInputAmount").read[Int] and
      (__ \ "postAccessDefinedContributionInputAmount").read[Int] and
      (__ \ "taxYearSchemes").read[List[TaxYearScheme]] and
      (__ \ "totalIncome").read[Int] and
      (__ \ "chargePaidByMember").read[Int] and
      (__ \ "period").read[Period] and
      (__ \ "income").readNullable[Income] and
      (__ \ "definedBenefitInput2016PostAmount").readNullable[Int] and
      (__ \ "definedContributionInput2016PostAmount").readNullable[Int] and
      (__ \ "postAccessDefinedContributionInput2016PostAmount").readNullable[Int])(
      TaxYear2016To2023.InitialFlexiblyAccessedTaxYear
    )

    val postFlexiblyAccessedReads: Reads[TaxYear2016To2023] =
      ((__ \ "definedBenefitInputAmount").read[Int] and
        (__ \ "definedContributionInputAmount").read[Int] and
        (__ \ "totalIncome").read[Int] and
        (__ \ "chargePaidByMember").read[Int] and
        (__ \ "taxYearSchemes").read[List[TaxYearScheme]] and
        (__ \ "period").read[Period] and
        (__ \ "income").readNullable[Income] and
        (__ \ "definedBenefitInput2016PostAmount").readNullable[Int] and
        (__ \ "definedContributionInput2016PostAmount").readNullable[Int])(
        TaxYear2016To2023.PostFlexiblyAccessedTaxYear
      )

    (__ \ "period")
      .read[Period]
      .flatMap[Period] {
        case p if p >= Period._2016 && p <= Period._2023 =>
          Reads(_ => JsSuccess(p))
        case _                                           =>
          Reads(_ => JsError("tax year must be `between 2016-pre and 2023`"))
      }
      .andKeep(normalReads orElse initialReads orElse postFlexiblyAccessedReads)
  }

  implicit lazy val writes: Writes[TaxYear2016To2023] = {

    import play.api.libs.functional.syntax._

    lazy val normalWrites: Writes[TaxYear2016To2023.NormalTaxYear] = (
      (__ \ "pensionInputAmount").write[Int] and
        (__ \ "taxYearSchemes").write[List[TaxYearScheme]] and
        (__ \ "totalIncome").write[Int] and
        (__ \ "chargePaidByMember").write[Int] and
        (__ \ "period").write[Period] and
        (__ \ "income").writeNullable[Income] and
        (__ \ "pensionInput2016PostAmount").writeNullable[Int]
    )(a =>
      (
        a.pensionInputAmount,
        a.taxYearSchemes,
        a.totalIncome,
        a.chargePaidByMember,
        a.period,
        a.income,
        a.pensionInput2016PostAmount
      )
    )

    lazy val initialWrites: Writes[TaxYear2016To2023.InitialFlexiblyAccessedTaxYear] = (
      (__ \ "definedBenefitInputAmount").write[Int] and
        (__ \ "flexiAccessDate").write[LocalDate] and
        (__ \ "preAccessDefinedContributionInputAmount").write[Int] and
        (__ \ "postAccessDefinedContributionInputAmount").write[Int] and
        (__ \ "taxYearSchemes").write[List[TaxYearScheme]] and
        (__ \ "totalIncome").write[Int] and
        (__ \ "chargePaidByMember").write[Int] and
        (__ \ "period").write[Period] and
        (__ \ "income").writeNullable[Income] and
        (__ \ "definedBenefitInput2016PostAmount").writeNullable[Int] and
        (__ \ "definedContributionInput2016PostAmount").writeNullable[Int] and
        (__ \ "postAccessDefinedContributionInput2016PostAmount").writeNullable[Int]
    )(a =>
      (
        a.definedBenefitInputAmount,
        a.flexiAccessDate,
        a.preAccessDefinedContributionInputAmount,
        a.postAccessDefinedContributionInputAmount,
        a.taxYearSchemes,
        a.totalIncome,
        a.chargePaidByMember,
        a.period,
        a.income,
        a.definedBenefitInput2016PostAmount,
        a.definedContributionInput2016PostAmount,
        a.postAccessDefinedContributionInput2016PostAmount
      )
    )

    lazy val postWrites: Writes[TaxYear2016To2023.PostFlexiblyAccessedTaxYear] = (
      (__ \ "definedBenefitInputAmount").write[Int] and
        (__ \ "definedContributionInputAmount").write[Int] and
        (__ \ "totalIncome").write[Int] and
        (__ \ "chargePaidByMember").write[Int] and
        (__ \ "taxYearSchemes").write[List[TaxYearScheme]] and
        (__ \ "period").write[Period] and
        (__ \ "income").writeNullable[Income] and
        (__ \ "definedBenefitInput2016PostAmount").writeNullable[Int] and
        (__ \ "definedContributionInput2016PostAmount").writeNullable[Int]
    )(a =>
      (
        a.definedBenefitInputAmount,
        a.definedContributionInputAmount,
        a.totalIncome,
        a.chargePaidByMember,
        a.taxYearSchemes,
        a.period,
        a.income,
        a.definedBenefitInput2016PostAmount,
        a.definedContributionInput2016PostAmount
      )
    )

    Writes {
      case year: TaxYear2016To2023.NormalTaxYear =>
        Json.toJson(year)(normalWrites)

      case year: TaxYear2016To2023.InitialFlexiblyAccessedTaxYear =>
        Json.toJson(year)(initialWrites)

      case year: TaxYear2016To2023.PostFlexiblyAccessedTaxYear =>
        Json.toJson(year)(postWrites)
    }
  }

}
