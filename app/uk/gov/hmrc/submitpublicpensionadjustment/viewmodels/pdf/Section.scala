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

package uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf

import play.api.i18n.Messages
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.response.Period
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.sections.{CompensationSection, TaxAdministrationFrameworkSection}

import java.lang.reflect.Field

trait Section {

  private val messagePrefix =
    s"pdf.${getClass.getSimpleName.substring(0, 1).toLowerCase() + getClass.getSimpleName.substring(1)}"

  def rows(messages: Messages): Seq[Row] = {
    val fieldNames: Seq[String] = orderedFieldNames()

    def getDisplayLabelAndValue(fieldName: String, fieldValue: Any): (String, String) = {
      val displayValue = fieldValue match {
        case Some(s: String) => s
        case None            => "deleteRow"
        case s: String       => s
        case _               => "error"
      }

      val baseDataLabel = messages(s"$messagePrefix.$fieldName")

      val displayLabel = period() match {
        case Some(period) =>
          val periodLabel = messages(s"pdf.${period.toString}")
          baseDataLabel.replace("$period", periodLabel)
        case None         => baseDataLabel
      }

      (displayLabel, displayValue)
    }

    val regularRows = fieldNames.flatMap { fieldName =>
      val field: Field = getClass.getDeclaredField(fieldName)
      field.setAccessible(true)
      val fieldValue   = field.get(this)
      Some(Row.tupled(getDisplayLabelAndValue(fieldName, fieldValue)))
    }

    this match {
      case compensationSection: CompensationSection =>
        val additionalRows = compensationSection.additionalRows.map { case (label, value) =>
          Row.tupled(getDisplayLabelAndValue(label, value))
        }
        regularRows ++ additionalRows
      case taxAdministrationFrameworkSection: TaxAdministrationFrameworkSection =>
        val additionalRows = taxAdministrationFrameworkSection.additionalRows.map { case (label, value) =>
          Row.tupled(getDisplayLabelAndValue(label, value))
        }
        val (firstPart, secondPart) = regularRows.splitAt(2)
        firstPart ++ additionalRows ++ secondPart
      case _ => regularRows
    }
  }

  def orderedFieldNames(): Seq[String]

  def period(): Option[Period] = None

  def displayLines(messages: Messages): Seq[String] =
    rows(messages).map(row => row.displayLabel + " : " + row.displayValue)
}
