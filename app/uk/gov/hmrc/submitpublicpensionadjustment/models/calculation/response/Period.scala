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

import play.api.Logging
import play.api.libs.json._
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.{Period => InputsPeriod}

import scala.util.{Failure, Success, Try}

sealed trait Period {

  def toCalculationInputsPeriod: InputsPeriod =
    this match {
      case Period._2011 => InputsPeriod._2011
      case Period._2012 => InputsPeriod._2012
      case Period._2013 => InputsPeriod._2013
      case Period._2014 => InputsPeriod._2014
      case Period._2015 => InputsPeriod._2015
      case Period._2016 => InputsPeriod._2016
      case Period._2017 => InputsPeriod._2017
      case Period._2018 => InputsPeriod._2018
      case Period._2019 => InputsPeriod._2019
      case Period._2020 => InputsPeriod._2020
      case Period._2021 => InputsPeriod._2021
      case Period._2022 => InputsPeriod._2022
      case Period._2023 => InputsPeriod._2023
    }
}

object Period extends Logging {

  private val JsonValue2016Pre  = "2016-pre"
  private val JsonValue2016Post = "2016-post"

  case class Year(year: Int) extends Period {

    override lazy val toString: String = year.toString
  }

  case object _2016PreAlignment extends Period {

    override lazy val toString: String = "2016-pre"
  }

  case object _2016PostAlignment extends Period {

    override lazy val toString: String = "2016-post"
  }

  val _2011: Period = Period.Year(2011)
  val _2012: Period = Period.Year(2012)
  val _2013: Period = Period.Year(2013)
  val _2014: Period = Period.Year(2014)
  val _2015: Period = Period.Year(2015)
  val _2016: Period = Period.Year(2016)
  val _2017: Period = Period.Year(2017)
  val _2018: Period = Period.Year(2018)
  val _2019: Period = Period.Year(2019)
  val _2020: Period = Period.Year(2020)
  val _2021: Period = Period.Year(2021)
  val _2022: Period = Period.Year(2022)
  val _2023: Period = Period.Year(2023)

  implicit lazy val reads: Reads[Period] =
    __.read[String].flatMap { case yearString =>
      Try(yearString.toInt) match {
        case Success(year) if year >= 2011 =>
          Reads(_ => JsSuccess(Year(year)))
        case Success(year)                 =>
          Reads(_ => JsError(s"year: `$year`, must be 2011 or later"))
        case Failure(_)                    =>
          Reads(_ => JsError("invalid tax year"))
      }
    }

  implicit lazy val writes: Writes[Period] = Writes { case Period.Year(year) =>
    JsString(year.toString)
  }

  implicit lazy val ordering: Ordering[Period] =
    new Ordering[Period] {
      override def compare(x: Period, y: Period): Int =
        (x, y) match {
          case (Period.Year(a), Period.Year(b))                      => a compare b
          case (Period._2016PreAlignment, Period.Year(b))            => 2016 compare b
          case (Period._2016PostAlignment, Period.Year(b))           => 2016 compare b
          case (Period.Year(a), Period._2016PreAlignment)            => a compare 2016
          case (Period.Year(a), Period._2016PostAlignment)           => a compare 2016
          case (Period._2016PostAlignment, Period._2016PreAlignment) => 1
          case (Period._2016PreAlignment, Period._2016PostAlignment) => -1
          case (Period._2016PreAlignment, Period._2016PreAlignment) |
              (Period._2016PostAlignment, Period._2016PostAlignment) =>
            0
        }
    }

}
