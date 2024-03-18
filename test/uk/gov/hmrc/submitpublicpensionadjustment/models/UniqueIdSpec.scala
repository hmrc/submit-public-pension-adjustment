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

package uk.gov.hmrc.submitpublicpensionadjustment.models

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper

class UniqueIdSpec extends AnyFreeSpec {

  "UniqueId" - {

    "must be constructed when string is valid" in {
      val validUuidString = "12341234-1234-1234-1234-123412341234"

      val uniqueIdOption: Option[Either[String, Option[UniqueId]]] = UniqueId.fromString(validUuidString)

      uniqueIdOption mustBe Some(Right(Some(UniqueId(validUuidString))))
    }

    "must not be constructed when string is invalid" in {
      val uniqueIdOption: Option[Either[String, Option[UniqueId]]] = UniqueId.fromString("invalidUniqueId")

      uniqueIdOption mustBe Some(Left("invalid param format"))
    }
  }
}
