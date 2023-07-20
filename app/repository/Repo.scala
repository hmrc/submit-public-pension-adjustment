/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package repository

import org.bson.codecs.Codec
import org.mongodb.scala.model.{Filters, IndexModel, ReplaceOptions}
import play.api.libs.json._
import repository.Repo.{Id, IdExtractor}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

abstract class Repo[ID, A: ClassTag](
    collectionName: String,
    mongoComponent: MongoComponent,
    indexes:        Seq[IndexModel],
    extraCodecs:    Seq[Codec[_]]   = Seq.empty,
    replaceIndexes: Boolean         = false
)(implicit
    manifest: Manifest[A],
  domainFormat:     OFormat[A],
  executionContext: ExecutionContext,
  id:               Id[ID],
  idExtractor:      IdExtractor[A, ID]
)
  extends PlayMongoRepository[A](
    mongoComponent = mongoComponent,
    collectionName = collectionName,
    domainFormat   = domainFormat,
    extraCodecs    = extraCodecs,
    indexes        = indexes,
    replaceIndexes = replaceIndexes
  ) {

  /**
   * Update or Insert (upsert) element `a` identified by `id`
   */
  def upsert(a: A): Future[Unit] = collection
    .replaceOne(
      filter      = Filters.eq("_id", id.value(idExtractor.id(a))),
      replacement = a,
      options     = ReplaceOptions().upsert(true)
    )
    .toFuture()
    .map(_ => ())

  def findById(i: ID): Future[Option[A]] = collection
    .find(
      filter = Filters.eq("_id", id.value(i))
    )
    .headOption()
}

object Repo {
  trait Id[I] {
    def value(i: I): String
  }

  trait IdExtractor[A, ID] {
    def id(a: A): ID
  }

}
