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

import org.apache.pdfbox.pdmodel.PDDocument
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.i18n.MessagesApi
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.Calculation
import uk.gov.hmrc.submitpublicpensionadjustment.views.xml.CalculationPdf

import java.nio.file.{Files, Paths}
import java.time.Instant
import scala.io.Source

class FopServiceSpec extends AnyFreeSpec with Matchers with ScalaFutures with IntegrationPatience {

  private val app = GuiceApplicationBuilder().build()
  private val fopService = app.injector.instanceOf[FopService]

  private val fixedInstant                   = Instant.now

  "render" - {

    "must render some fop content as a pdf" in {
      val input = Source.fromResource("fop/simple.fo").mkString
      val result = fopService.render(input).futureValue
      PDDocument.load(result)
    }

    "must generate a test PDF" in {

      val calculation = Calculation(
        nino = "nino",
        dataItem1 = "dataItem1",
        submissionReference = "submissionReference",
        created = fixedInstant
      )

      val view = app.injector.instanceOf[CalculationPdf]
      val messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())
      val xmlString = view.render(calculation, messages).body
      val result = fopService.render(xmlString).futureValue

      val fileName = "test/resources/fop/test.pdf"
      Files.write(Paths.get(fileName), result)
    }
  }
}
