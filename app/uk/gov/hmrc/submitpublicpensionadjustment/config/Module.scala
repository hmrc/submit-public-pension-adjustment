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

package uk.gov.hmrc.submitpublicpensionadjustment.config

import org.apache.fop.apps.FopFactory
import play.api.inject.Binding
import play.api.{Configuration, Environment}
import uk.gov.hmrc.submitpublicpensionadjustment.services.{CreateLocalPdfDmsSubmissionService, DefaultDmsSubmissionService, DmsSubmissionService, NoOpDmsSubmissionService}

import java.time.Clock

class Module extends play.api.inject.Module {

  override def bindings(environment: Environment, configuration: Configuration): collection.Seq[Binding[_]] = {

    val authTokenInitialiserBinding: Binding[InternalAuthTokenInitialiser] =
      if (configuration.get[Boolean]("internal-auth-token-initialiser.enabled")) {
        bind[InternalAuthTokenInitialiser].to[InternalAuthTokenInitialiserImpl].eagerly()
      } else bind[InternalAuthTokenInitialiser].to[NoOpInternalAuthTokenInitialiser].eagerly()

    val dmsSubmissionServiceBinding: Binding[DmsSubmissionService] =
      if (configuration.get[Boolean]("dms-submission.enabled")) {
        bind[DmsSubmissionService].to[DefaultDmsSubmissionService].eagerly()
      } else {
        if (configuration.get[Boolean]("dms-submission.createLocalPdf")) {
          bind[DmsSubmissionService].to[CreateLocalPdfDmsSubmissionService].eagerly()
        } else {
          bind[DmsSubmissionService].to[NoOpDmsSubmissionService].eagerly()
        }
      }

    Seq(
      bind[AppConfig].toSelf.eagerly(),
      bind[Clock].toInstance(Clock.systemUTC()),
      bind[FopFactory].toProvider[FopFactoryProvider].eagerly(),
      authTokenInitialiserBinding,
      dmsSubmissionServiceBinding
    )
  }
}
