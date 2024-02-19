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

package uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.response

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.{Period => InputsPeriod}

class ResponsePeriodSpec extends AnyFlatSpec with Matchers {

  "toCalculationInputsPeriod" should "correctly map Period instances to InputsPeriod instances" in {
    Period._2011.toCalculationInputsPeriod shouldBe InputsPeriod._2011
    Period._2012.toCalculationInputsPeriod shouldBe InputsPeriod._2012
    Period._2013.toCalculationInputsPeriod shouldBe InputsPeriod._2013
    Period._2014.toCalculationInputsPeriod shouldBe InputsPeriod._2014
    Period._2015.toCalculationInputsPeriod shouldBe InputsPeriod._2015
    Period._2016.toCalculationInputsPeriod shouldBe InputsPeriod._2016
    Period._2017.toCalculationInputsPeriod shouldBe InputsPeriod._2017
    Period._2018.toCalculationInputsPeriod shouldBe InputsPeriod._2018
    Period._2019.toCalculationInputsPeriod shouldBe InputsPeriod._2019
    Period._2020.toCalculationInputsPeriod shouldBe InputsPeriod._2020
    Period._2021.toCalculationInputsPeriod shouldBe InputsPeriod._2021
    Period._2022.toCalculationInputsPeriod shouldBe InputsPeriod._2022
    Period._2023.toCalculationInputsPeriod shouldBe InputsPeriod._2023
  }
}
