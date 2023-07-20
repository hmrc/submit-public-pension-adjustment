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

package uk.gov.hmrc.submitpublicpensionadjustment.models

import play.api.libs.json.{Format, Json}

import scala.util.matching.Regex

sealed abstract class TaxId(
    val desIdType: String,
    val value:     String,
    val regex:     Regex,
    val desRegime: String //TODO: Does it need to be here and does it need to be a String?
) {
  def isValid: Boolean =
    value match {
      case regex(_*) => true
      case _         => false
    }
}

/**
 * Tax id for Soft Drinks Industry Levy (Sdil)
 *
 * If you wonder why it was names such:
 * Smart people renamed it from Sdil to Zsdl:
 * 1> They prepended `Z` do the Sdil word to sort it as last identifier on their list of all identifiers
 * 2> They removed `I` in order to reduce identifier to get only 4 characters in it
 * TADA !!!
 */
final case class Zsdl(override val value: String) extends TaxId(
  desIdType = "zsdl",
  value,
  /*Regex from https://github.com/hmrc/service-enrolment-config/blob/master/conf/SEC1_with_enrolment_rules_json/prod/HMRC-OBTDS-ORG.json*/
  regex     = "^[0-9A-Za-z]{1,15}$".r, // TODO: Retrieve from config? Different to regex for Sdil
  desRegime = "zsdl"
)

object Zsdl {
  implicit val format: Format[Zsdl] = Json.valueFormat
}

/* Regex from https://github.com/hmrc/service-enrolment-config/blob/master/conf/SEC1_with_enrolment_rules_json/prod/HMRC-MTD-VAT.json */
final case class Vrn(override val value: String) extends TaxId(
  desIdType = "vrn",
  value,
  "^[0-9]{1,9}$".r, // TODO: Retrieve from config?
  desRegime = "vatc"
)

object Vrn {
  implicit val format: Format[Vrn] = Json.valueFormat
}

/**
 * EORI stands for “Economic Operators Registration and Identification number”.
 * https://confluence.tools.tax.service.gov.uk/display/CD/EORI+-+Solution+Architecture+Design
 */
final case class Eori(value: String)

object Eori {
  val regime: String = "cds"
  val desIdType: String = "eori"

  implicit val format: Format[Eori] = Json.valueFormat
}

final case class Dan(override val value: String) extends TaxId(
  desIdType = "dan",
  value,
  "^\\d{7}$".r, // A Deferment Account Number is 7 digits
  desRegime = "cds"
)

object Dan {
  // DDI refs relating to CDS contain the DAN in the first 7 digits
  def fromDdiRef(ddiRef: String): Dan = Dan(ddiRef.take(7))

  implicit val format: Format[Dan] = Json.valueFormat
}

final case class Zppt(override val value: String) extends TaxId(
  desIdType = "zppt",
  value,
  /*Regex from https://github.com/hmrc/service-enrolment-config/blob/master/conf/SEC1_with_enrolment_rules_json/prod/HMRC-OBTDS-ORG.json*/
  regex     = "^X[A-Z]PPT000[0-9]{7}$".r, // TODO: Retrieve from config?
  desRegime = "ppt"
)

object Zppt {
  implicit val format: Format[Zppt] = Json.valueFormat
}

final case class EmpRef(override val value: String) extends TaxId(
  desIdType = "empref",
  value,
  EmpRef.regex,
  desRegime = "paye"
)

object EmpRef {
  val regex = "^\\d{3}[A-Za-z0-9]{1,10}$".r

  val taxOfficeNumberRegex = "^\\d{3}$".r
  val taxOfficeReferenceRegex = "^[A-Za-z0-9]{1,10}$".r

  implicit val format: Format[EmpRef] = Json.valueFormat
}
