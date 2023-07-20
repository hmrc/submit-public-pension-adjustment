package bars

import bars.BarsVerifyStatusRepo._
import uk.gov.hmrc.submitpublicpensionadjustment.models.bars.{BarsVerifyStatus, BarsVerifyStatusId, EncryptedBarsVerifyStatus}
import org.mongodb.scala.model.{IndexModel, IndexOptions, Indexes}
import repository.Repo
import repository.Repo.{Id, IdExtractor}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.Codecs
import uk.gov.hmrc.submitpublicpensionadjustment.config.{AppConfig, BarsConfig}

import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
final class BarsVerifyStatusRepo @Inject() (
                                             mongoComponent: MongoComponent,
                                             config:         AppConfig
                                           )(implicit ec: ExecutionContext)
  extends Repo[String, EncryptedBarsVerifyStatus](
    collectionName = "bars",
    mongoComponent = mongoComponent,
    indexes        = BarsVerifyStatusRepo.indexes(config.barsVerifyRepoTtl.toSeconds),
    extraCodecs    = Codecs.playFormatCodecsBuilder(EncryptedBarsVerifyStatus.format).build,
    replaceIndexes = false
  )

object BarsVerifyStatusRepo {
  implicit val id: Id[String] = (i: String) => i
  implicit val idExtractor: IdExtractor[EncryptedBarsVerifyStatus, String] = (b: EncryptedBarsVerifyStatus) => b._id

  def indexes(cacheTtlInSeconds: Long): Seq[IndexModel] = Seq(
    IndexModel(
      keys         = Indexes.ascending("lastUpdated"),
      indexOptions = IndexOptions().expireAfter(cacheTtlInSeconds, TimeUnit.SECONDS).name("lastUpdatedIdx")
    )
  )
}