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

package uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission

import play.api.libs.functional.syntax.*
import play.api.libs.json.{Format, __}

case class SubmissionInputs(
  administrativeDetails: AdministrativeDetails,
  paymentElections: List[PaymentElection],
  calculationInputSchemeIdentifiers: List[IndividualSchemeIdentifier],
  schemeTaxRelief: Option[SchemeTaxRelief],
  bankAccountDetails: Option[BankAccountDetails],
  declarations: Declarations
) {}

object SubmissionInputs {

  implicit lazy val formats: Format[SubmissionInputs] = (
    (__ \ "administrativeDetails").format[AdministrativeDetails] and
      (__ \ "paymentElections").format[List[PaymentElection]] and
      (__ \ "calculationInputSchemeIdentifiers").format[List[IndividualSchemeIdentifier]] and
      (__ \ "schemeTaxRelief").formatNullable[SchemeTaxRelief] and
      (__ \ "bankAccountDetails").formatNullable[BankAccountDetails] and
      (__ \ "declarations").format[Declarations]
  )(SubmissionInputs.apply, o => Tuple.fromProductTyped(o))
}
