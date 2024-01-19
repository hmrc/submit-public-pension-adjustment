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

import org.mockito.ArgumentMatchers.anyString
import org.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json._
import uk.gov.hmrc.submitpublicpensionadjustment.models.Enumerable

class EnumerableSpec extends PlaySpec with MockitoSugar {

  object TestImplicits extends Enumerable.Implicits

  "Enumerable" should {

    "correctly deserialize a valid string to an object" in {
      val mockEnumerable: Enumerable[DummyType] = mock[Enumerable[DummyType]]
      when(mockEnumerable.withName("valid")).thenReturn(Some(DummyType("valid")))

      implicit val reads: Reads[DummyType] = TestImplicits.reads(mockEnumerable)

      val json = JsString("valid")
      json.validate[DummyType] mustEqual JsSuccess(DummyType("valid"))
    }

    "return JsError for an invalid string" in {
      val mockEnumerable: Enumerable[DummyType] = mock[Enumerable[DummyType]]
      when(mockEnumerable.withName(anyString)).thenReturn(None)

      implicit val reads: Reads[DummyType] = TestImplicits.reads(mockEnumerable)

      val json = JsString("invalid")
      json.validate[DummyType] mustBe a[JsError]
    }

    "return JsError for non-JsString JSON" in {
      val mockEnumerable: Enumerable[DummyType] = mock[Enumerable[DummyType]]
      when(mockEnumerable.withName(anyString)).thenReturn(None)

      implicit val reads: Reads[DummyType] = TestImplicits.reads(mockEnumerable)

      val json = JsNumber(123)
      json.validate[DummyType] mustBe a[JsError]
    }
  }

  case class DummyType(name: String)
}