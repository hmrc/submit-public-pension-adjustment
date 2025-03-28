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

case class BankAccountDetails(
  accountName: String,
  sortCode: String,
  accountNumber: String,
  rollNumber: Option[String]
) {}

object BankAccountDetails {

  implicit lazy val formats: Format[BankAccountDetails] = (
    (__ \ "accountName").format[String] and
      (__ \ "sortCode").format[String] and
      (__ \ "accountNumber").format[String] and
      (__ \ "rollNumber").formatNullable[String]
  )(BankAccountDetails.apply, o => Tuple.fromProductTyped(o))
}
