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

package uk.gov.hmrc.submitpublicpensionadjustment.models.response

import akka.util.Helpers.Requiring
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.JsString
import play.api.mvc.PathBindable
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.Period
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.Period.{Year, _2016PostAlignment, _2016PreAlignment}

import scala.util.Try

class PeriodSpec extends AnyFlatSpec with Matchers {

  "Period Model" should "serialise JsString to period" in {

    val json   = JsString("2017")
    val result = Try(json.as[Period])

    result.get shouldEqual Period.Year(2017)
  }

  "Period Model" should "return correct period from string" in {

    val invalidString = "123"
    val yearString    = "2017"
    val preString     = "2016-pre"
    val postString    = "2016-post"

    Period.fromString(invalidString) shouldEqual None
    Period.fromString(yearString)    shouldEqual Some(Year(2017))
    Period.fromString(preString)     shouldEqual Some(_2016PreAlignment)
    Period.fromString(postString)    shouldEqual Some(_2016PostAlignment)
  }

  "Period Model" should "should return PathBindable period from string when string is a valid tax year" in {

    val pathBindable = implicitly[PathBindable[Period]]
    val period       = Period._2017

    val bind: Either[String, Period] = pathBindable.bind("", "2017")
    bind.value mustBe Right(period)
  }

  "Period Model" should "return fold left and error when string is not a valid tax year" in {

    val pathBindable = implicitly[PathBindable[Period]]

    val bind: Either[String, Period] = pathBindable.bind("", "aaaa")
    bind.value mustBe Left("Invalid tax year")
  }

  "Period Model" should "unbind year from period and return as a string" in {

    val pathBindable = implicitly[PathBindable[Period]]

    val bindValue = pathBindable.unbind("", Period._2017)
    bindValue mustBe "2017"
  }
}
