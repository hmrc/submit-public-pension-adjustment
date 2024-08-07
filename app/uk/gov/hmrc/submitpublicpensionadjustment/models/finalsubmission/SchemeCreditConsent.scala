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

import uk.gov.hmrc.submitpublicpensionadjustment.models.{Enumerable, WithName}

sealed trait SchemeCreditConsent

object SchemeCreditConsent extends Enumerable.Implicits {

  case object Yes extends WithName("yes") with SchemeCreditConsent

  val values: Seq[Boolean] = Seq(
    true
  )

  implicit lazy val enumerable: Enumerable[Boolean] =
    Enumerable(values.map(v => v.toString -> v): _*)

}
