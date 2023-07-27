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

sealed trait TaxYearSchemeCalculation {

  def name: String

  def pensionSchemeTaxReference: String
}

case class OutOfDatesTaxYearSchemeCalculation(name: String, pensionSchemeTaxReference: String, compensation: Int)

object OutOfDatesTaxYearSchemeCalculation {

  implicit lazy val reads: Reads[OutOfDatesTaxYearSchemeCalculation] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "name").read[String] and
        (__ \ "pensionSchemeTaxReference").read[String] and
        (__ \ "compensation").read[Int]
    )(OutOfDatesTaxYearSchemeCalculation.apply _)

  }

  implicit lazy val writes: Writes[OutOfDatesTaxYearSchemeCalculation] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "name").write[String] and
        (__ \ "pensionSchemeTaxReference").write[String] and
        (__ \ "compensation").write[Int]
    )(a =>
      (
        a.name,
        a.pensionSchemeTaxReference,
        a.compensation
      )
    )
  }
}

case class InDatesTaxYearSchemeCalculation(name: String, pensionSchemeTaxReference: String, chargePaidByScheme: Int)

object InDatesTaxYearSchemeCalculation {

  implicit lazy val reads: Reads[InDatesTaxYearSchemeCalculation] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "name").read[String] and
        (__ \ "pensionSchemeTaxReference").read[String] and
        (__ \ "chargePaidByScheme").read[Int]
    )(InDatesTaxYearSchemeCalculation.apply _)

  }

  implicit lazy val writes: Writes[InDatesTaxYearSchemeCalculation] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "name").write[String] and
        (__ \ "pensionSchemeTaxReference").write[String] and
        (__ \ "chargePaidByScheme").write[Int]
    )(a =>
      (
        a.name,
        a.pensionSchemeTaxReference,
        a.chargePaidByScheme
      )
    )
  }

}
