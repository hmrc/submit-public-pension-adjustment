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

import org.scalatestplus.play.PlaySpec
class TaxRateSpec extends PlaySpec {
  "NonScottishTaxRate" should {
    "apply correct tax rates for different years" in {
      NonScottishTaxRate._2016().getTaxRate(10000) mustBe 0.00
      NonScottishTaxRate._2017().getTaxRate(20000) mustBe 0.20
      NonScottishTaxRate._2018().getTaxRate(50000) mustBe 0.40
      NonScottishTaxRate._2019().getTaxRate(160000) mustBe 0.45
    }
  }

  "ScottishTaxRateAfter2018" should {
    "apply correct tax rates for different years" in {
      ScottishTaxRateAfter2018._2019().getTaxRate(11850) mustBe 0.00
      ScottishTaxRateAfter2018._2019().getTaxRate(12000) mustBe 0.19
      ScottishTaxRateAfter2018._2020().getTaxRate(24944) mustBe 0.20
      ScottishTaxRateAfter2018._2021().getTaxRate(35000) mustBe 0.21
      ScottishTaxRateAfter2018._2022().getTaxRate(45000) mustBe 0.41
      ScottishTaxRateAfter2018._2023().getTaxRate(160000) mustBe 0.46
    }
  }
}
