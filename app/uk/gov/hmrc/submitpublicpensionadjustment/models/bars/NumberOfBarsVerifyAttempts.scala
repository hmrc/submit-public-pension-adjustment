/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.submitpublicpensionadjustment.models.bars

import play.api.libs.json.{Format, Json}

final case class NumberOfBarsVerifyAttempts(value: Int) extends AnyVal

object NumberOfBarsVerifyAttempts {

  val zero: NumberOfBarsVerifyAttempts = NumberOfBarsVerifyAttempts(0)

  implicit class NumberOfBarsVerifyAttemptsOps(private val n: NumberOfBarsVerifyAttempts) {

    def increment: NumberOfBarsVerifyAttempts = NumberOfBarsVerifyAttempts(n.value + 1)

  }

  implicit val format: Format[NumberOfBarsVerifyAttempts] = Json.valueFormat

}
