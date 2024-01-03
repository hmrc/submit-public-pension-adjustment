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

package uk.gov.hmrc.submitpublicpensionadjustment.services

import org.mockito.MockitoSugar
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.submitpublicpensionadjustment.TestData.submissionInputs
import uk.gov.hmrc.submitpublicpensionadjustment.models.SubmissionAuditEvent
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.Income.BelowThreshold
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.TaxYear2016To2023.PostFlexiblyAccessedTaxYear
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.{AnnualAllowance, CalculationInputs, Period => InputsPeriod, Resubmission => InputsResubmission, TaxYear2011To2015}
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.response.{CalculationResponse, OutOfDatesTaxYearSchemeCalculation, OutOfDatesTaxYearsCalculation, Period => ResponsePeriod, Resubmission => ResponseResubmission, TaxYearScheme, TotalAmounts}
import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.FinalSubmission

class AuditServiceSpec extends AnyFreeSpec with Matchers with MockitoSugar {

  private val mockAuditConnector = mock[AuditConnector]

  private val app = GuiceApplicationBuilder()
    .overrides(
      bind[AuditConnector].toInstance(mockAuditConnector)
    )
    .configure(
      "auditing.submission-request-event-name" -> "Submission"
    )
    .build()

  private val service = app.injector.instanceOf[AuditService]

  implicit val hc = HeaderCarrier()

  "AuditService" - {

    "auditSubmitRequest" - {
      "should call the audit connector with the Submission event" in {

        val calculationInputs = CalculationInputs(
          InputsResubmission(false, None),
          Some(
            AnnualAllowance(
              List(InputsPeriod._2021, InputsPeriod._2019, InputsPeriod._2017),
              List(
                TaxYear2011To2015(20000, InputsPeriod._2011),
                TaxYear2011To2015(40000, InputsPeriod._2013),
                TaxYear2011To2015(40000, InputsPeriod._2014),
                TaxYear2011To2015(40000, InputsPeriod._2015),
                PostFlexiblyAccessedTaxYear(
                  33000,
                  0,
                  60000,
                  0,
                  List(
                    TaxYearScheme("Scheme 1", "00348916RT", 20000, 15000, 0),
                    TaxYearScheme("Scheme 2", "00348916RG", 20000, 18000, 0)
                  ),
                  InputsPeriod._2016PreAlignment
                ),
                PostFlexiblyAccessedTaxYear(
                  40000,
                  0,
                  60000,
                  3200,
                  List(
                    TaxYearScheme("Scheme 1", "00348916RT", 30000, 25000, 0),
                    TaxYearScheme("Scheme 2", "00348916RG", 18000, 15000, 0)
                  ),
                  InputsPeriod._2016PostAlignment
                ),
                PostFlexiblyAccessedTaxYear(
                  38000,
                  0,
                  60000,
                  1200,
                  List(
                    TaxYearScheme("Scheme 1", "00348916RT", 28000, 25000, 0),
                    TaxYearScheme("Scheme 2", "00348916RG", 15000, 13000, 0)
                  ),
                  InputsPeriod._2017,
                  Some(BelowThreshold)
                ),
                PostFlexiblyAccessedTaxYear(
                  38000,
                  0,
                  60000,
                  0,
                  List(
                    TaxYearScheme("Scheme 1", "00348916RT", 0, 0, 0),
                    TaxYearScheme("Scheme 2", "00348916RG", 30000, 25000, 0)
                  ),
                  InputsPeriod._2018,
                  Some(BelowThreshold)
                )
              )
            )
          ),
          None
        )

        val calculationResponse = CalculationResponse(
          ResponseResubmission(false, None),
          TotalAmounts(0, 0, 0),
          List(
            OutOfDatesTaxYearsCalculation(
              ResponsePeriod._2016PreAlignment,
              0,
              0,
              0,
              0,
              0,
              0,
              34000,
              List(
                OutOfDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0),
                OutOfDatesTaxYearSchemeCalculation("Scheme 2", "00348916RG", 0)
              )
            ),
            OutOfDatesTaxYearsCalculation(
              ResponsePeriod._2016PostAlignment,
              0,
              0,
              3200,
              0,
              46000,
              18400,
              0,
              List(
                OutOfDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0),
                OutOfDatesTaxYearSchemeCalculation("Scheme 2", "00348916RG", 0)
              )
            ),
            OutOfDatesTaxYearsCalculation(
              ResponsePeriod._2017,
              0,
              0,
              1200,
              0,
              36000,
              14400,
              0,
              List(
                OutOfDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0),
                OutOfDatesTaxYearSchemeCalculation("Scheme 2", "00348916RG", 0)
              )
            ),
            OutOfDatesTaxYearsCalculation(
              ResponsePeriod._2018,
              0,
              0,
              0,
              0,
              23000,
              9200,
              0,
              List(
                OutOfDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0),
                OutOfDatesTaxYearSchemeCalculation("Scheme 2", "00348916RG", 0)
              )
            )
          ),
          List()
        )

        val finalSubmission = FinalSubmission(calculationInputs, Some(calculationResponse), submissionInputs)

        val submissionAuditEvent = SubmissionAuditEvent("internalId", Individual, None, finalSubmission)

        service.auditSubmitRequest(submissionAuditEvent)(hc) mustBe ()

      }
    }

  }
}
