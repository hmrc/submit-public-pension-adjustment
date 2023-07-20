package bars

import cats.data.OptionT
import uk.gov.hmrc.submitpublicpensionadjustment.config.BarsConfig
import uk.gov.hmrc.submitpublicpensionadjustment.models.bars.{BarsVerifyStatus, BarsVerifyStatusResponse, NumberOfBarsVerifyAttempts, TaxIdKey}

import java.time.temporal.ChronoUnit
import java.time.{Clock, Instant}
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BarsVerifyStatusService @Inject() (
                                          barsRepo:   BarsVerifyStatusRepo,
                                          barsConfig: BarsConfig,
                                          clock:      Clock
                                        )(implicit ec: ExecutionContext) {

  /*
   * get current count of calls to verify endpoint for this taxId
   */
  def status(taxId: TaxIdKey): Future[BarsVerifyStatusResponse] =
    find(taxId).map {
      case Some(barsStatus) => BarsVerifyStatusResponse(barsStatus)
      case None =>
        BarsVerifyStatusResponse(
          attempts              = NumberOfBarsVerifyAttempts.zero,
          lockoutExpiryDateTime = None
        )
    }

  /*
   * increment the verify call count for this taxId
   * and if it exceeds maxAttempts then set the expiryDateTime field
   */
  def update(taxId: TaxIdKey): Future[BarsVerifyStatusResponse] =
    OptionT[Future, BarsVerifyStatus](find(taxId))
      .fold(BarsVerifyStatus(taxId)) {
        status =>
          val newVerifyCalls = status.verifyCalls.increment
          val expiry: Option[Instant] =
            if (newVerifyCalls.value >= barsConfig.barsVerifyMaxAttempts)
              Some(Instant.now(clock).plus(24, ChronoUnit.HOURS))
            else None

          status.copy(
            verifyCalls           = newVerifyCalls,
            lastUpdated           = Instant.now,
            lockoutExpiryDateTime = expiry
          )
      }
      .flatMap(status => upsert(status).map(_ => BarsVerifyStatusResponse(status)))

  private def find(taxId: TaxIdKey): Future[Option[BarsVerifyStatus]] =
    barsRepo.findById(taxId)

  private def upsert(barsStatus: BarsVerifyStatus): Future[Unit] =
    barsRepo.upsert(barsStatus)

}