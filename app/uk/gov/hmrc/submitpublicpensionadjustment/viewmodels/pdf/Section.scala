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

package uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf

import play.api.i18n.Messages
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.response.Period

import java.lang.reflect.Field

trait Section {

  def messagePrefix =
    s"pdf.${getClass.getSimpleName.substring(0, 1).toLowerCase() + getClass.getSimpleName.substring(1)}"

  def rows(messages: Messages): Seq[Row] = {
    val fieldNames: Seq[String] = orderedFieldNames()

    fieldNames.flatMap { fieldName =>
      val field: Field = getClass.getDeclaredField(fieldName)
      field.setAccessible(true)
      val fieldValue   = field.get(this)
      getDisplayLabelAndValue(messages, fieldName, fieldValue, false)
    }
  }

  def orderedFieldNames(): Seq[String]

  def period(): Option[Period] = None

  def displayLines(messages: Messages): Seq[String] =
    rows(messages).map(row => row.displayLabel + " : " + row.displayValue)

  def getDisplayLabelAndValue(messages: Messages, fieldName: String, fieldValue: Any, indent: Boolean): Option[Row] = {

    val label: String = displayLabel(messages, fieldName)

    fieldValue match {
      case Some(value: String) => Some(Row(label, value, indent))
      case None                => None
      case value: String       => Some(Row(label, value, indent))
      case _                   => Some(Row(label, "error", indent))
    }
  }

  def displayLabel(messages: Messages, fieldName: String): String = {
    val baseDataLabel = messages(s"$messagePrefix.$fieldName")

    period() match {
      case Some(period) =>
        val periodLabel = messages(s"pdf.${period.toString}")
        baseDataLabel.replace("&period", periodLabel)
      case None         => baseDataLabel
    }
  }
}
