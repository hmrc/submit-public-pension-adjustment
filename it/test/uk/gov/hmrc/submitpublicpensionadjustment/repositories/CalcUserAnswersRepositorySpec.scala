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

package uk.gov.hmrc.submitpublicpensionadjustment.repositories

import org.mockito.Mockito.when
import org.scalatest.concurrent.Eventually.eventually
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, OptionValues}
import org.scalatestplus.mockito.MockitoSugar
import play.api.Configuration
import play.api.libs.json.Json
import uk.gov.hmrc.crypto.{Decrypter, Encrypter, SymmetricCryptoFactory}
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport
import uk.gov.hmrc.submitpublicpensionadjustment.config.AppConfig
import uk.gov.hmrc.submitpublicpensionadjustment.models.{CalcUserAnswers, Done}
import uk.gov.hmrc.submitpublicpensionadjustment.utils.WireMockHelper

import java.security.SecureRandom
import java.time.temporal.ChronoUnit
import java.time.{Clock, Instant, ZoneId}
import java.util.Base64
import scala.concurrent.ExecutionContext.Implicits.global

class CalcUserAnswersRepositorySpec
    extends AnyFreeSpec
    with Matchers
    with DefaultPlayMongoRepositorySupport[CalcUserAnswers]
    with ScalaFutures
    with IntegrationPatience
    with OptionValues
    with MockitoSugar
      with BeforeAndAfterEach
      with BeforeAndAfterAll
      with WireMockHelper {

  override def beforeAll(): Unit = {
    super.beforeAll()
    startWireMock()
  }

  override def afterAll(): Unit = {
    stopWireMock()
    super.afterAll()
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    resetWireMock()
  }

  private val userAnswersId    = "userAnswersId"
  private val userAnswersId2   = "userAnswersId2"
  private val instant          = Instant.now.truncatedTo(ChronoUnit.MILLIS)
  private val stubClock: Clock = Clock.fixed(instant, ZoneId.systemDefault)
  private val mockAppConfig    = mock[AppConfig]

  private val aesKey = {
    val aesKey = new Array[Byte](32)
    new SecureRandom().nextBytes(aesKey)
    Base64.getEncoder.encodeToString(aesKey)
  }

  private val configuration = Configuration("crypto.key" -> aesKey)

  private implicit val crypto: Encrypter with Decrypter =
    SymmetricCryptoFactory.aesGcmCryptoFromConfig("crypto", configuration.underlying)

  when(mockAppConfig.ttlInDays) thenReturn 900.toLong

  protected override val repository: CalcUserAnswersRepository = new CalcUserAnswersRepository(
    mongoComponent = mongoComponent,
    appConfig = mockAppConfig,
    clock = stubClock
  )

  val userAnswers =
    CalcUserAnswers(userAnswersId, Json.obj("foo" -> "bar"), "uniqueId", Instant.now(stubClock), true, true)

  ".get" - {

    "when a userAnswer exists, must get the record with the Id" in {

      insert(userAnswers).futureValue

      val result = repository.get(userAnswersId).futureValue
      eventually(Timeout(Span(30, Seconds))) {
        result.value mustEqual userAnswers
      }
    }

    "when no userAnswer exists, return None" in {

      repository.get(userAnswersId).futureValue must not be defined
    }
  }

    ".getByUniqueId" - {

      "when a userAnswer exists, must get the record with the uniqueId" in {

        insert(userAnswers).futureValue

        val result = repository.getByUniqueId("uniqueId").futureValue
        result.value mustEqual userAnswers
      }

      "when no userAnswer exists, return None" in {

        repository.getByUniqueId("uniqueId").futureValue must not be defined
      }
    }

  ".clear" - {

    "must clear user answers" in {

      insert(userAnswers).futureValue

      repository.clear(userAnswersId).futureValue
      repository.get(userAnswersId).futureValue must not be defined
    }
  }

  ".set" - {

    "must set user answers" in {

      repository.set(userAnswers).futureValue
      repository.get(userAnswersId).futureValue.value mustBe userAnswers
    }
  }

  ".keepAlive" - {

    "must return done when last updated time kept alive" in {

      insert(userAnswers).futureValue

      repository.keepAlive(userAnswersId).futureValue mustBe Done
    }
  }

    ".keepAliveByUniqueId" - {

      "must return done when last updated time kept alive" in {

        insert(userAnswers).futureValue

        repository.keepAliveByUniqueId("uniqueId").futureValue mustBe Done
      }
    }

  ".clearByUniqueIdAndNotId" - {

    "must clear user answers with UniqueId with different Id" in {

      insert(userAnswers).futureValue
      insert(userAnswers.copy(id = userAnswersId2)).futureValue

      repository.clearByUniqueIdAndNotId("uniqueId", userAnswersId).futureValue
      repository.get(userAnswersId).futureValue mustBe defined
      repository.get(userAnswersId2).futureValue must not be defined
    }
  }
}
