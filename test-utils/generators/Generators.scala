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

package generators

import org.scalacheck.Gen.*
import org.scalacheck.{Gen, Shrink}

import java.time.*

trait Generators {

  implicit val noShrink: Shrink[String] = Shrink.shrinkAny

  def intsBelowValue(value: Int): Gen[Int] = Gen.chooseNum(0, value)

  def stringsWithMaxLength(maxLength: Int): Gen[String] =
    for {
      length <- choose(1, maxLength)
      chars  <- listOfN(length, Gen.alphaNumChar)
    } yield chars.mkString

  def localDateTimeGen: Gen[LocalDateTime] = {
    val rangeEnd = LocalDateTime.now(Clock.systemUTC()).toEpochSecond(ZoneOffset.UTC)
    Gen
      .choose(0.toLong, rangeEnd)
      .map(second => LocalDateTime.ofEpochSecond(second, 0, ZoneOffset.UTC))
  }
}
