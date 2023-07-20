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

package bars

import cats.data.OptionT
import repository.MongoCrypto
import uk.gov.hmrc.submitpublicpensionadjustment.config.AppConfig
import uk.gov.hmrc.submitpublicpensionadjustment.models.bars._

import java.time.temporal.ChronoUnit
import java.time.{Clock, Instant}
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BarsVerifyStatusService @Inject() (
                                          barsRepo:   BarsVerifyStatusRepo,
                                          config:     AppConfig,
                                          crypto:     MongoCrypto,
                                          clock:      Clock
                                        )(implicit ec: ExecutionContext) {

  import crypto._

  /*
   * get current count of calls to verify endpoint for this id
   */
  def status(id: BarsVerifyStatusId): Future[BarsVerifyStatusResponse] =
    find(id).map {
      case Some(barsStatus) => BarsVerifyStatusResponse(barsStatus)
      case None =>
        BarsVerifyStatusResponse(
          attempts              = NumberOfBarsVerifyAttempts.zero,
          lockoutExpiryDateTime = None
        )
    }

  /*
   * increment the verify call count for this id
   * and if it exceeds maxAttempts then set the expiryDateTime field
   */
  def update(id: BarsVerifyStatusId): Future[BarsVerifyStatusResponse] =
    OptionT[Future, BarsVerifyStatus](find(id))
      .fold(BarsVerifyStatus(id)) {
        status =>
          val newVerifyCalls = status.verifyCalls.increment
          val expiry: Option[Instant] =
            if (newVerifyCalls.value >= config.barsVerifyMaxAttempts)
              Some(Instant.now(clock).plus(24, ChronoUnit.HOURS))
            else None

          status.copy(
            verifyCalls           = newVerifyCalls,
            lastUpdated           = Instant.now,
            lockoutExpiryDateTime = expiry
          )
      }
      .flatMap(status => upsert(status).map(_ => BarsVerifyStatusResponse(status)))


  private def find(id: BarsVerifyStatusId): Future[Option[BarsVerifyStatus]] = {
    barsRepo
      .findById(encryptStr(id.value))
      .map {
        case Some(enc) => Some(decrypt(enc))
        case None => None
      }
  }

  private def upsert(barsStatus: BarsVerifyStatus): Future[Unit] =
    barsRepo.upsert(encrypt(barsStatus))

  private def decrypt(enc: EncryptedBarsVerifyStatus): BarsVerifyStatus =
    BarsVerifyStatus(
      _id = BarsVerifyStatusId(decryptStr(enc._id)),
      verifyCalls = enc.verifyCalls,
      createdAt = enc.createdAt,
      lastUpdated = enc.lastUpdated,
      lockoutExpiryDateTime = enc.lockoutExpiryDateTime
    )

  private def encrypt(barsStatus: BarsVerifyStatus): EncryptedBarsVerifyStatus =
    EncryptedBarsVerifyStatus(
      _id = encryptStr(barsStatus._id.value),
      verifyCalls = barsStatus.verifyCalls,
      createdAt = barsStatus.createdAt,
      lastUpdated = barsStatus.lastUpdated,
      lockoutExpiryDateTime = barsStatus.lockoutExpiryDateTime
    )

}