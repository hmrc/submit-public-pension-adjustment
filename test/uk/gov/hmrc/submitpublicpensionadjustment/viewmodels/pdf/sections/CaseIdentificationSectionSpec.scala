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

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import uk.gov.hmrc.submitpublicpensionadjustment.models._
import uk.gov.hmrc.submitpublicpensionadjustment.models.dms.{Compensation, CompensationAmendment, LTA, MiniRegime, MiniRegimeAmendment}
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.sections.CaseIdentificationSection

import java.time.LocalDate

class CaseIdentificationSectionSpec extends AnyFreeSpec with Matchers {

  "CaseIdentificationSection" - {
    "should be correctly constructed with all queue references" in {
      val caseIdentifiers = CaseIdentifiers("testCaseNumber",
        queueReferences = List(
          QueueReference(Compensation("testQueue"), "compRef"),
          QueueReference(CompensationAmendment("testQueue"), "compAmendRef"),
          QueueReference(MiniRegime("testQueue"), "miniRegimeRef"),
          QueueReference(MiniRegimeAmendment("testQueue"), "miniRegimeAmendRef"),
          QueueReference(LTA("testQueue"), "ltaRef")
        )
      )

      val section = CaseIdentificationSection.build(caseIdentifiers)

      section mustBe CaseIdentificationSection(
        compensation = "compRef",
        compensationAmendment = "compAmendRef",
        miniRegime = "miniRegimeRef",
        miniRegimeAmendment = "miniRegimeAmendRef",
        lta = "ltaRef"
      )
    }

    "should correctly handle missing queue references" in {
      val caseIdentifiers = CaseIdentifiers("testCaseNumber",
        queueReferences = List(
        )
      )

      val section = CaseIdentificationSection.build(caseIdentifiers)

      section mustBe CaseIdentificationSection(
        compensation = "Not Applicable",
        compensationAmendment = "Not Applicable",
        miniRegime = "Not Applicable",
        miniRegimeAmendment = "Not Applicable",
        lta = "Not Applicable"
      )
    }
  }
}
