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

package uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs

import uk.gov.hmrc.submitpublicpensionadjustment.models.{Enumerable, WithName}

sealed trait WhatNewProtectionTypeEnhancement

object WhatNewProtectionTypeEnhancement extends Enumerable.Implicits {

  case object EnhancedProtection extends WithName(s"enhancedProtection") with WhatNewProtectionTypeEnhancement
  case object PrimaryProtection extends WithName(s"primaryProtection") with WhatNewProtectionTypeEnhancement
  case object FixedProtection extends WithName(s"fixedProtection") with WhatNewProtectionTypeEnhancement
  case object FixedProtection2014 extends WithName(s"fixedProtection2014") with WhatNewProtectionTypeEnhancement
  case object FixedProtection2016 extends WithName(s"fixedProtection2016") with WhatNewProtectionTypeEnhancement
  case object IndividualProtection2014
      extends WithName(s"individualProtection2014")
      with WhatNewProtectionTypeEnhancement
  case object IndividualProtection2016
      extends WithName(s"individualProtection2016")
      with WhatNewProtectionTypeEnhancement

  val values: Seq[WhatNewProtectionTypeEnhancement] = Seq(
    EnhancedProtection,
    PrimaryProtection,
    FixedProtection,
    FixedProtection2014,
    FixedProtection2016,
    IndividualProtection2014,
    IndividualProtection2016
  )

  implicit val enumerable: Enumerable[WhatNewProtectionTypeEnhancement] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
