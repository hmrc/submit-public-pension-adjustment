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

package uk.gov.hmrc.submitpublicpensionadjustment.services

import akka.stream.scaladsl.Source
import akka.util.ByteString
import play.api.i18n.{Messages, MessagesApi}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.submitpublicpensionadjustment.connectors.DmsSubmissionConnector
import uk.gov.hmrc.submitpublicpensionadjustment.models.Done
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.Calculation
import uk.gov.hmrc.submitpublicpensionadjustment.views.xml.CalculationPdf

import java.nio.file.{Files, Paths}
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

abstract class DmsSubmissionService {

  def submitCalculation(calculation: Calculation)(implicit hc: HeaderCarrier): Future[Done]
}

@Singleton
class NoOpDmsSubmissionService @Inject() () extends DmsSubmissionService {
  override def submitCalculation(calculation: Calculation)(implicit hc: HeaderCarrier): Future[Done] =
    Future.successful(Done)
}

@Singleton
class DefaultDmsSubmissionService @Inject() (
  dmsConnector: DmsSubmissionConnector,
  fopService: FopService,
  pdfTemplate: CalculationPdf,
  messagesApi: MessagesApi
)(implicit ec: ExecutionContext)
    extends DmsSubmissionService {

  private implicit val messages: Messages =
    messagesApi.preferred(Seq.empty)

  override def submitCalculation(calculation: Calculation)(implicit hc: HeaderCarrier): Future[Done] =
    for {
      pdfBytes <- fopService.render(pdfTemplate(calculation).body)
      _        <- submitToDms(calculation, pdfBytes, calculation.submissionReference)
    } yield Done

  private def submitToDms(calculation: Calculation, pdfBytes: Array[Byte], submissionReference: String)(implicit
    hc: HeaderCarrier
  ): Future[Done] =
    dmsConnector.submitCalculation(
      customerId = calculation.nino,
      pdf = Source.single(ByteString(pdfBytes)),
      timestamp = calculation.created,
      submissionReference = submissionReference
    )
}
