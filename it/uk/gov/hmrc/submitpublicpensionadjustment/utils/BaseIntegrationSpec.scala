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

package uk.gov.hmrc.submitpublicpensionadjustment.utils

import generators.ModelGenerators
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, EitherValues}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.submitpublicpensionadjustment.config.AppConfig

import scala.concurrent.ExecutionContext

trait BaseIntegrationSpec
    extends PlaySpec
    with ScalaFutures
    with IntegrationPatience
    with EitherValues
    with GuiceOneServerPerSuite
    with BeforeAndAfterAll
    with TableDrivenPropertyChecks
    with BeforeAndAfterEach
    with ModelGenerators {

  implicit lazy val headerCarrier: HeaderCarrier = HeaderCarrier()

  val baseUrl = s"http://localhost:$port"

  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .configure("metrics.enabled" -> false)
      .configure("auditing.enabled" -> false)
      .build()

  implicit lazy val ec: ExecutionContext = fakeApplication().injector.instanceOf[ExecutionContext]
  lazy val wsClient: WSClient            = fakeApplication().injector.instanceOf[WSClient]
  lazy val httpClient: HttpClientV2      = fakeApplication().injector.instanceOf[HttpClientV2]
  lazy val appConfig: AppConfig          = fakeApplication().injector.instanceOf[AppConfig]

  val requestHeaders: Set[(String, String)] = Set(
    ("Authorization", s"Bearer someEncryptedToken")
  )
}
