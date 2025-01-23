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

package uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs

import play.api.libs.json._
import play.api.mvc.PathBindable

import java.time.LocalDate
import scala.util.{Failure, Success, Try}

sealed trait Period {

  def start: LocalDate
  def end: LocalDate
}

object Period {

  case class Year(year: Int) extends Period {

    override lazy val toString: String = year.toString
    override lazy val start: LocalDate = LocalDate.of(year - 1, 4, 6)
    override lazy val end: LocalDate   = LocalDate.of(year, 4, 5)
  }

  case object _2016PreAlignment extends Period {

    override lazy val toString: String = "2016-pre"
    override lazy val start: LocalDate = LocalDate.of(2015, 4, 6)
    override lazy val end: LocalDate   = LocalDate.of(2015, 7, 8)
  }

  case object _2016PostAlignment extends Period {

    override lazy val toString: String = "2016-post"
    override lazy val start: LocalDate = LocalDate.of(2015, 7, 9)
    override lazy val end: LocalDate   = LocalDate.of(2016, 4, 5)
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
  case `_2016PreAlignment` =>
    JsString(_2016PreAlignment.toString)
  case `_2016PostAlignment` =>
    JsString(_2016PostAlignment.toString)
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

  def fromString(string: String): Option[Period] =
    string match {
      case yearString =>
        Try(yearString.toInt) match {
          case Success(year) if year >= 2011 && year <= 2023 => Some(Year(year))
          case _                                             => None
        }
    }

  implicit def indexPathBindable(implicit stringBinder: PathBindable[String]): PathBindable[Period] =
    new PathBindable[Period] {

      override def bind(key: String, periodString: String): Either[String, Period] =
        fromString(periodString) match {
          case Some(taxYear) => Right(taxYear)
          case None          => Left("Invalid tax year")
        }

      override def unbind(key: String, period: Period): String =
        stringBinder.unbind(key, period.toString)
    }
}
