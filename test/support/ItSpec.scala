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

package support

import com.google.inject.{AbstractModule, Provides}
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.http.HeaderNames
import play.api.inject.bind
import play.api.inject.guice.{GuiceApplicationBuilder, GuiceableModule}
import play.api.libs.json.{JsValue, Json, OWrites}
import play.api.test.{DefaultAwaitTimeout, FakeRequest}
import repository.{MongoCrypto}
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys}
import uk.gov.hmrc.mongo.test.MongoSupport
import uk.gov.hmrc.submitpublicpensionadjustment.models.journey.JourneyId
import uk.gov.hmrc.submitpublicpensionadjustment.util.WireMockSupport

import java.time.Clock
import java.util.UUID
import javax.inject.Singleton

trait ItSpec extends UnitSpec with GuiceOneServerPerSuite
  with DefaultAwaitTimeout with WireMockSupport
  with BeforeAndAfterAll with BeforeAndAfterEach
  with MongoSupport
  with GlobalExecutionContext {

  val longTimeout: Timeout = Timeout(scaled(Span(10, Seconds)))

  val journeyId: JourneyId = JourneyId(UUID.randomUUID().toString)

  val hc: HeaderCarrier = HeaderCarrier()

  protected lazy val configOverrides: Map[String, Any] = Map()

  protected lazy val configMap: Map[String, Any] = Map[String, Any](
    "jvm.attribute.vendor" -> "",
    "mongodb.uri" -> "mongodb://localhost:27017/submit-public-pension-adjustment-it",
    "play.http.router" -> "testOnlyDoNotUseInAppConf.Routes",
    "metrics.enabled" -> false,
    "journeyVariables.minimumUpfrontPaymentAmountInPence" -> 100L,
    "auditing.consumer.baseUri.port" -> WireMockSupport.port,
    "microservice.services.integration-framework.port" -> WireMockSupport.port,
    "microservice.services.auth.port" -> WireMockSupport.port,
    "microservice.services.nrs-service.port" -> WireMockSupport.port,
    "auditing.traceRequests" -> false,
    "auditing.enabled" -> false
  ) ++ configOverrides

  lazy val overridingsModule: AbstractModule = new AbstractModule {
    @Provides
    @Singleton
    def clock: Clock = {
      FrozenTime.reset()
      FrozenTime.getClock
    }
  }

  lazy val modules: List[GuiceableModule] =
    List(
      bind[MongoCrypto].toInstance(testMongoCrypto)
    )

  val overridingGuiceableModule: GuiceableModule = GuiceableModule.fromGuiceModules(List(overridingsModule))

  // in tests use `app`
  override def fakeApplication(): Application = new GuiceApplicationBuilder()
    .overrides(modules :+ overridingGuiceableModule: _*)
    .configure(configMap)
    .build()


  implicit class AuthRequest[+A]( request:FakeRequest[A]) {
    def withAuthToken(authToken: String = "authToken"): FakeRequest[A] =
      request.withHeaders(HeaderNames.AUTHORIZATION -> authToken)

    def withJsonContentType() =
      request.withHeaders(HeaderNames.CONTENT_TYPE -> "text/json")

    def withSessionId(sessionId: String = rndSessionId) =
      request.withSession(SessionKeys.sessionId -> sessionId)

  }

  def rndSessionId = s"session-${UUID.randomUUID.toString}"

  val testMongoCrypto = new MongoCrypto {
    override def encryptStr[A](s: A): String = s.toString
    override def decryptStr[A](s: A): String = s.toString

    override def encryptJson[A](s: A)(implicit write: OWrites[A]): String = Json.toJson(s).toString
    override def decryptJson[A](s: A): JsValue = Json.parse(s.toString)
  }

}
