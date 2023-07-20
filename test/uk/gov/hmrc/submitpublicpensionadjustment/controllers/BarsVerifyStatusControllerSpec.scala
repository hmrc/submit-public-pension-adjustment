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

package uk.gov.hmrc.submitpublicpensionadjustment.controllers

import bars.{BarsVerifyStatusController, BarsVerifyStatusRepo}
import org.mongodb.scala.bson.BsonDocument
import play.api.http.Status._
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, status}
import repository.MongoCrypto
import support.{FrozenTime, ItSpec}
import uk.gov.hmrc.submitpublicpensionadjustment.models.Nino
import uk.gov.hmrc.submitpublicpensionadjustment.models.bars.{BarsUpdateVerifyStatusParams, BarsVerifyStatusId, EncryptedBarsVerifyStatus, NumberOfBarsVerifyAttempts}
import uk.gov.hmrc.submitpublicpensionadjustment.util.stubs.AuthStub

import java.time.temporal.ChronoUnit.HOURS

class BarsVerifyStatusControllerSpec extends ItSpec {

  val repo: BarsVerifyStatusRepo = app.injector.instanceOf[BarsVerifyStatusRepo]
  val crypto: MongoCrypto = app.injector.instanceOf[MongoCrypto]

  trait Setup {
    AuthStub.authorise()

    val controller: BarsVerifyStatusController = app.injector.instanceOf[BarsVerifyStatusController]

    val ninoABC: Nino = Nino("AB123456C")

    def updateVerifyStatusParams(nino: Nino): BarsUpdateVerifyStatusParams =
      BarsUpdateVerifyStatusParams(BarsVerifyStatusId.from(nino))

    def request(nino: Nino): FakeRequest[BarsUpdateVerifyStatusParams] =
      FakeRequest().withAuthToken().withBody(updateVerifyStatusParams(nino))

    def initialStatusAttempts(nino: Nino, attempts: Int): Unit =
      repo.collection.insertOne(
        EncryptedBarsVerifyStatus(
          _id = crypto.encryptStr(BarsVerifyStatusId.from(nino).value),
          verifyCalls = NumberOfBarsVerifyAttempts(attempts)
        )
      ).toFuture().map(_ => ()).futureValue
  }

  "the bars verify status controller" when {
    "called for an Id with no record" should {
      "respond with body 'zero attempts'" in new Setup {
        val response = controller.status()(request(ninoABC))
        status(response) shouldBe OK
        contentAsString(response) shouldBe """{"attempts":0}"""
      }
    }

    "called for an Id with an existing record of 'one attempt'" should {
      "respond with body 'one attempt'" in new Setup {
        initialStatusAttempts(ninoABC, attempts = 1)
        val response = controller.status()(request(ninoABC))
        status(response) shouldBe OK
        contentAsString(response) shouldBe """{"attempts":1}"""
      }
    }
  }

  "the bars verify update controller" when {
    "called for an Id with no record" should {
      "respond with body 'one attempt'" in new Setup {
        val response = controller.update()(request(ninoABC))
        status(response) shouldBe OK
        contentAsString(response) shouldBe """{"attempts":1}"""
      }
    }

    "called for an Id with an existing record of 'one attempt'" should {
      "respond with body 'two attempts'" in new Setup {
        initialStatusAttempts(ninoABC, attempts = 1)
        val response = controller.update()(request(ninoABC))
        status(response) shouldBe OK
        contentAsString(response) shouldBe """{"attempts":2}"""
      }
    }

    "called for an Id with an existing record of 'two attempts'" should {
      "respond with locked-out body" in new Setup {
        initialStatusAttempts(ninoABC, attempts = 2)
        val response = controller.update()(request(ninoABC))
        status(response) shouldBe OK

        val expectedLockout = FrozenTime.instant.plus(24, HOURS)
        contentAsString(response) shouldBe s"""{"attempts":3,"lockoutExpiryDateTime":"${expectedLockout}"}"""
      }
    }
  }


  override def beforeEach(): Unit = {
    super.beforeEach()
    repo.collection.deleteMany(BsonDocument("{}")).toFuture().futureValue
  }
}
