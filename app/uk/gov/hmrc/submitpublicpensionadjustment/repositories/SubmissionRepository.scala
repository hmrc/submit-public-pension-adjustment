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

import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model._
import uk.gov.hmrc.crypto.{Decrypter, Encrypter}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.submitpublicpensionadjustment.config.AppConfig
import uk.gov.hmrc.submitpublicpensionadjustment.models.Done
import uk.gov.hmrc.submitpublicpensionadjustment.models.submission.Submission

import java.time.{Clock, Instant}
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmissionRepository @Inject() (
  mongoComponent: MongoComponent,
  appConfig: AppConfig,
  clock: Clock
)(implicit ec: ExecutionContext, crypto: Encrypter with Decrypter)
    extends PlayMongoRepository[Submission](
      collectionName = "submissions",
      mongoComponent = mongoComponent,
      replaceIndexes = true,
      domainFormat = Submission.encryptedFormat,
      indexes = Seq(
        IndexModel(
          Indexes.ascending("lastUpdated"),
          IndexOptions()
            .name("lastUpdatedIdx")
            .expireAfter(appConfig.ttlInDays, TimeUnit.DAYS)
        ),
        IndexModel(
          Indexes.ascending("uniqueId"),
          IndexOptions()
            .name("uniqueIdx")
            .unique(true)
        )
      )
    ) {

  def insert(item: Submission): Future[Done] = {
    val updatedSubmission =
      item copy (lastUpdated = Instant.now(clock))

    collection
      .replaceOne(
        filter = byUserId(updatedSubmission.id),
        replacement = updatedSubmission,
        options = ReplaceOptions().upsert(true)
      )
      .toFuture
      .map(_ => Done)
  }

  private def byUniqueId(uniqueId: String): Bson = Filters.equal("uniqueId", uniqueId)

  private def byUserId(userId: String): Bson = Filters.equal("_id", userId)

  def keepAlive(userId: String): Future[Boolean] =
    collection
      .updateOne(
        filter = byUserId(userId),
        update = Updates.set("lastUpdated", Instant.now(clock))
      )
      .toFuture
      .map(_ => true)

  def get(uniqueId: String): Future[Option[Submission]] =
    collection
      .find(byUniqueId(uniqueId))
      .headOption()

  def getByUserId(userId: String): Future[Option[Submission]] =
    collection
      .find(byUserId(userId))
      .headOption()

  def clear(userId: String): Future[Boolean] =
    collection
      .deleteOne(byUserId(userId))
      .toFuture
      .map(_ => true)
}
