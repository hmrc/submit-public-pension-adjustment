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

case class Declarations(
  compensation: Boolean,
  tax: Boolean,
  contactDetails: Boolean,
  powerOfAttorney: Option[Boolean],
  claimOnBehalfOfDeceased: Option[Boolean],
  legalPersonalRepresentative: Option[Boolean],
  schemeCreditConsent: Option[Boolean]
) {}

object Declarations {

  implicit lazy val formats: Format[Declarations] = (
    (__ \ "compensation").format[Boolean] and
      (__ \ "tax").format[Boolean] and
      (__ \ "contactDetails").format[Boolean] and
      (__ \ "powerOfAttorney").formatNullable[Boolean] and
      (__ \ "claimOnBehalfOfDeceased").formatNullable[Boolean] and
      (__ \ "legalPersonalRepresentative").formatNullable[Boolean] and
      (__ \ "schemeCreditConsent").formatNullable[Boolean]
  )(Declarations.apply, o => Tuple.fromProductTyped(o))
}
