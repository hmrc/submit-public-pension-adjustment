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

package uk.gov.hmrc.submitpublicpensionadjustment.viewmodels

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.Logging
import play.api.i18n.MessagesApi
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import uk.gov.hmrc.submitpublicpensionadjustment.TestData
import uk.gov.hmrc.submitpublicpensionadjustment.models.CaseIdentifiers
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.PDFViewModel
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.sections.{AdministrativeDetailsSection, DeclarationsSection}

import java.nio.file.{Files, Paths}

class PDFViewModelSpec extends AnyFreeSpec with Matchers with Logging {

  private val app      = GuiceApplicationBuilder().build()
  private val messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())

  "PDFViewModel" - {

    "must support pretty printing to aid diagnostics" in {

      val prettyPrintLines = TestData.viewModel.prettyPrint(messages)
      logger.info(s"pdfViewModel :\n$prettyPrintLines\n")

      val fileName = "test/resources/final_submission.txt"
      Files.write(Paths.get(fileName), prettyPrintLines.getBytes())
    }

    "must be constructed from a minimal final submission" in {

      val caseIdentifiers = CaseIdentifiers("1234", Seq())

      val viewModel: PDFViewModel = PDFViewModel.build(caseIdentifiers, TestData.finalSubmission)

      viewModel.caseNumber mustBe "1234"

      viewModel.administrativeDetailsSection mustBe AdministrativeDetailsSection(
        firstName = "FirstName",
        surname = "Surname",
        dob = "dob",
        addressLine1 = "addressLine1",
        addressLine2 = "addressLine2",
        postCode = "postCode",
        country = Some("country"),
        utr = Some("utr"),
        ninoOrTrn = "ninoOrTrn",
        contactNumber = "contactNumber"
      )

      viewModel.declarationsSection mustBe DeclarationsSection("Y", "Y", "Y", "Y", "Y", "Y", "Y")
    }
  }
}
