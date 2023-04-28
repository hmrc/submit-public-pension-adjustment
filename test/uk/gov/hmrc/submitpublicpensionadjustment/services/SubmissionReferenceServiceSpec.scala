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

package uk.gov.hmrc.submitpublicpensionadjustment.services

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class SubmissionReferenceServiceSpec extends AnyFreeSpec with Matchers {

  "random" - {

    "must generate valid submission references" in {

      val service = new SubmissionReferenceService()
      val ids = Vector.fill(100)(service.random())
      val pattern = """^[\dA-Z]{4}(-?)[\dA-Z]{4}\1[\dA-Z]{4}$"""

      ids.foreach { id =>
        id must fullyMatch regex pattern
      }
    }
  }
}
