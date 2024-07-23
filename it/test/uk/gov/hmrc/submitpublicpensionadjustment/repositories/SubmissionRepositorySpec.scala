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

import org.mockito.MockitoSugar
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.model.Filters
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.Configuration
import play.api.libs.json.Json
import uk.gov.hmrc.crypto.{Crypted, Decrypter, Encrypter, SymmetricCryptoFactory}
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport
import uk.gov.hmrc.submitpublicpensionadjustment.config.AppConfig
import uk.gov.hmrc.submitpublicpensionadjustment.models.Done
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.{CalculationInputs, Resubmission}
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.response.{CalculationResponse, InDatesTaxYearSchemeCalculation, InDatesTaxYearsCalculation, OutOfDatesTaxYearSchemeCalculation, OutOfDatesTaxYearsCalculation, TotalAmounts, Period => responsePeriod, Resubmission => ResponseResubmission}
import uk.gov.hmrc.submitpublicpensionadjustment.models.submission.Submission
import uk.gov.hmrc.submitpublicpensionadjustment.repositories.SubmissionRepository
import uk.gov.hmrc.submitpublicpensionadjustment.utils.WireMockHelper

import java.security.SecureRandom
import java.time.temporal.ChronoUnit
import java.time.{Clock, Instant, ZoneId}
import java.util.Base64
import scala.concurrent.ExecutionContext.Implicits.global

class SubmissionRepositorySpec
    extends AnyFreeSpec
    with Matchers
    with DefaultPlayMongoRepositorySupport[Submission]
    with ScalaFutures
    with IntegrationPatience
    with OptionValues
    with MockitoSugar
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

  private val id                 = "id"
  private val submissionUniqueId = "submissionUniqueId"
  private val instant            = Instant.now.truncatedTo(ChronoUnit.MILLIS)
  private val stubClock: Clock   = Clock.fixed(instant, ZoneId.systemDefault)

  private val mockAppConfig = mock[AppConfig]

  private val aesKey = {
    val aesKey = new Array[Byte](32)
    new SecureRandom().nextBytes(aesKey)
    Base64.getEncoder.encodeToString(aesKey)
  }

  private val configuration = Configuration("crypto.key" -> aesKey)

  private implicit val crypto: Encrypter with Decrypter =
    SymmetricCryptoFactory.aesGcmCryptoFromConfig("crypto", configuration.underlying)

  private val calculationInputs = CalculationInputs(Resubmission(false, None), None, None)
  val calculation               = Some(
    CalculationResponse(
      ResponseResubmission(false, None),
      TotalAmounts(10470, 1620, 5500),
      List(
        OutOfDatesTaxYearsCalculation(
          responsePeriod._2016,
          0,
          0,
          0,
          0,
          0,
          0,
          40000,
          List(OutOfDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0))
        )
      ),
      List(
        InDatesTaxYearsCalculation(
          responsePeriod._2020,
          0,
          4500,
          0,
          0,
          9000,
          10000,
          4500,
          0,
          List(InDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 9000))
        )
      )
    )
  )

  private val submission: Submission =
    Submission("id", "submissionUniqueId", calculationInputs, calculation)

  protected override val repository = new SubmissionRepository(
    mongoComponent = mongoComponent,
    appConfig = mockAppConfig,
    clock = stubClock
  )

  when(mockAppConfig.ttlInDays) thenReturn 900

  ".insert" - {

    "must set the last updated time to `now` and save the submission" in {

      val expectedResult = Submission(
        id,
        submissionUniqueId,
        calculationInputs,
        calculation,
        Instant.now(stubClock).truncatedTo(ChronoUnit.MILLIS)
      )

      val insertResult = repository.insert(submission).futureValue
      val dbRecord     = find(Filters.equal("uniqueId", submissionUniqueId)).futureValue.headOption.value

      insertResult mustEqual Done
      dbRecord mustEqual expectedResult
    }

    "must store the data section as encrypted bytes" in {

      val submission = Submission(
        id,
        submissionUniqueId,
        calculationInputs,
        calculation,
        Instant.now(stubClock).truncatedTo(ChronoUnit.MILLIS)
      )

      repository.insert(submission).futureValue

      val record = repository.collection
        .find[BsonDocument](Filters.equal("uniqueId", submission.uniqueId))
        .headOption()
        .futureValue
        .value

      checkDocumentIsEncryptedAndCanBeDecrypted(record)
    }
  }

  private def checkDocumentIsEncryptedAndCanBeDecrypted(record: BsonDocument) = {
    val json = Json.parse(record.toJson)

    val encryptedInputs: String = (json \ "calculationInputs").get.as[String]
    encryptedInputs mustNot include("resubmission")

    val plainInputs: String = decrypt(encryptedInputs)
    plainInputs must include("resubmission")

    val decryptedInputObject: CalculationInputs = Json.parse(plainInputs).as[CalculationInputs]
    decryptedInputObject.resubmission.isResubmission mustBe false

    val encryptedCalculation: String = (json \ "calculation").get.as[String]
    encryptedCalculation mustNot include("totalAmounts")

    val plainCalculation: String = decrypt(encryptedCalculation)
    plainCalculation must include("totalAmounts")

    val decryptedCalculationObject: CalculationResponse = Json.parse(plainCalculation).as[CalculationResponse]
    decryptedCalculationObject.totalAmounts.inDatesDebit mustBe 1620
  }

  private def decrypt(encryptedString: String): String = {
    val decryptedString: String = crypto.decrypt(Crypted(encryptedString)).value
    decryptedString.drop(1).dropRight(1).replaceAll("\\\\", "")
  }

  ".get" - {

    "when there is a record for this submissionUniqueId" - {

      "must get the record" in {

        val submission = Submission(
          id,
          submissionUniqueId,
          calculationInputs,
          None,
          Instant.now(stubClock).truncatedTo(ChronoUnit.MILLIS)
        )

        insert(submission).futureValue

        val result = repository.get(submissionUniqueId).futureValue
        result.value mustEqual submission
      }
    }

    "when there is no record for this submissionUniqueId" - {

      "must return None" in {

        repository.get(submissionUniqueId).futureValue must not be defined
      }
    }
  }

  ".keepAlive" - {

    "when there is a record for this id" - {

      "must update its lastUpdated to `now` and return true" in {

        val submission = Submission(
          id,
          submissionUniqueId,
          calculationInputs,
          None,
          Instant.now(stubClock).truncatedTo(ChronoUnit.MILLIS)
        )

        insert(submission).futureValue

        val result = repository.keepAlive(submission.id).futureValue

        val expectedSubmission = submission copy (lastUpdated = instant)

        result mustEqual true
        val updatedAnswers = find(Filters.equal("_id", "id")).futureValue.headOption.value
        updatedAnswers mustEqual expectedSubmission
      }
    }

    "when there is no record for this id" - {

      "must return true" in {

        repository.keepAlive("id that does not exist").futureValue mustEqual true
      }
    }
  }
}
