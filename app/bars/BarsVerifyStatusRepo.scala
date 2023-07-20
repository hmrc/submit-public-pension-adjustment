package bars

import bars.BarsVerifyStatusRepo._
import uk.gov.hmrc.submitpublicpensionadjustment.models.bars.{BarsVerifyStatus, TaxIdKey}
import org.mongodb.scala.model.{IndexModel, IndexOptions, Indexes}
import repository.Repo
import repository.Repo.{Id, IdExtractor}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.Codecs
import uk.gov.hmrc.submitpublicpensionadjustment.config.BarsConfig

import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
final class BarsVerifyStatusRepo @Inject() (
                                             mongoComponent: MongoComponent,
                                             config:         BarsConfig
                                           )(implicit ec: ExecutionContext)
  extends Repo[TaxIdKey, BarsVerifyStatus](
    collectionName = "bars",
    mongoComponent = mongoComponent,
    indexes        = BarsVerifyStatusRepo.indexes(config.barsVerifyRepoTtl.toSeconds),
    extraCodecs    = Codecs.playFormatCodecsBuilder(BarsVerifyStatus.format).build,
    replaceIndexes = true
  )

object BarsVerifyStatusRepo {
  implicit val taxId: Id[TaxIdKey] = (i: TaxIdKey) => i.value
  implicit val taxIdExtractor: IdExtractor[BarsVerifyStatus, TaxIdKey] = (b: BarsVerifyStatus) => b._id

  def indexes(cacheTtlInSeconds: Long): Seq[IndexModel] = Seq(
    IndexModel(
      keys         = Indexes.ascending("lastUpdated"),
      indexOptions = IndexOptions().expireAfter(cacheTtlInSeconds, TimeUnit.SECONDS).name("lastUpdatedIdx")
    )
  )
}