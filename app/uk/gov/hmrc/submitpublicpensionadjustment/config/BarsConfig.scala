/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.submitpublicpensionadjustment.config

import play.api.Configuration

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.FiniteDuration

@Singleton
class BarsConfig @Inject()(
    config: Configuration
) {
  val barsVerifyRepoTtl: FiniteDuration = config.get[FiniteDuration]("bars.verify.repoTtl")
  val barsVerifyMaxAttempts: Int = config.get[Int]("bars.verify.maxAttempts")
}
