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

import org.mockito.ArgumentMatchers.any
import org.mockito.{Mockito, MockitoSugar}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.{Configuration, Logging}
import uk.gov.hmrc.submitpublicpensionadjustment.TestData
import uk.gov.hmrc.submitpublicpensionadjustment.TestData.submissionInputs
import uk.gov.hmrc.submitpublicpensionadjustment.models.QueueReference
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.{AnnualAllowanceSetup, CalculationInputs, LifeTimeAllowance, LifetimeAllowanceSetup, MaybePIAIncrease, MaybePIAUnchangedOrDecreased, Resubmission => InputsResubmission, Setup}
import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.response._
import uk.gov.hmrc.submitpublicpensionadjustment.models.dms._
import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.FinalSubmission

class QueueLogicServiceSpec
    extends AnyFreeSpec
    with Matchers
    with MockitoSugar
    with BeforeAndAfterEach
    with ScalaFutures
    with IntegrationPatience
    with Logging {

  private val mockConfiguration              = mock[Configuration]
  private val mockDmsConfiguration           = mock[Configuration]
  private val mockSubmissionReferenceService = mock[SubmissionReferenceService]

  override def beforeEach(): Unit = {
    reset(
      mockConfiguration,
      mockSubmissionReferenceService
    )
    super.beforeEach()

    Mockito.when(mockConfiguration.get(any())(any())).thenReturn(mockDmsConfiguration)
    Mockito.when(mockDmsConfiguration.get(any())(any())).thenReturn("VGVzdF9RdWV1ZQ==")
  }

  "QueueLogicService" - {

    "must determine the most significant queue reference" in {
      val queueReferences =
        Seq(
          QueueReference(Compensation("Compensation_Queue"), "submissionReference1"),
          QueueReference(MiniRegime("MiniRegime_Queue"), "submissionReference2"),
          QueueReference(LTA("LTA_Queue"), "submissionReference3")
        )

      val queueReference = queueLogicService().determineMostSignificantQueueReference(queueReferences)

      queueReference mustBe QueueReference(MiniRegime("MiniRegime_Queue"), "submissionReference2")
    }

    "must compute a list of queue references" in {
      val queueReferences: Seq[QueueReference] = queueLogicService().computeQueueReferences(TestData.finalSubmission)

      queueReferences.size mustBe >=(1)
      queueReferences.count(q => q.dmsQueue.queueName() == "Test_Queue") mustBe >=(1)
    }
  }

  "QueueLogic" - {

    "Compensation must be included when out of date period has direct compensation" in {

      val resubmission = false

      val lta = None

      val calculationResponse: CalculationResponse = calculationResponseWith(
        directCompensation = 1000,
        indirectCompensation = 0,
        memberCredit = 0,
        schemeCredit = 0,
        debit = 0
      )

      val expectedDmsQueues = Seq(Compensation("Test_Queue"))

      checkQueues(queueLogicService(), resubmission, lta, calculationResponse, expectedDmsQueues)
    }

    "Compensation must be included when out of date period has indirect compensation" in {

      val resubmission = false

      val lta = None

      val calculationResponse: CalculationResponse = calculationResponseWith(
        directCompensation = 0,
        indirectCompensation = 1000,
        memberCredit = 0,
        schemeCredit = 0,
        debit = 0
      )

      val expectedDmsQueues = Seq(Compensation("Test_Queue"))

      checkQueues(queueLogicService(), resubmission, lta, calculationResponse, expectedDmsQueues)
    }

    "CompensationAmendment must be included when out of date period has direct compensation and is resubmission" in {

      val resubmission = true

      val lta = None

      val calculationResponse: CalculationResponse = calculationResponseWith(
        directCompensation = 1000,
        indirectCompensation = 0,
        memberCredit = 0,
        schemeCredit = 0,
        debit = 0
      )

      val expectedDmsQueues = Seq(CompensationAmendment("Test_Queue"))

      checkQueues(queueLogicService(), resubmission, lta, calculationResponse, expectedDmsQueues)
    }

    "CompensationAmendment must be included when out of date period has indirect compensation and is resubmission" in {

      val resubmission = true

      val lta = None

      val calculationResponse: CalculationResponse = calculationResponseWith(
        directCompensation = 0,
        indirectCompensation = 1000,
        memberCredit = 0,
        schemeCredit = 0,
        debit = 0
      )

      val expectedDmsQueues = Seq(CompensationAmendment("Test_Queue"))

      checkQueues(queueLogicService(), resubmission, lta, calculationResponse, expectedDmsQueues)
    }

    "MiniRegime must be included when member has credit" in {

      val resubmission = false

      val lta = None

      val calculationResponse: CalculationResponse = calculationResponseWith(
        directCompensation = 0,
        indirectCompensation = 0,
        memberCredit = 1,
        schemeCredit = 0,
        debit = 0
      )

      val expectedDmsQueues = Seq(MiniRegime("Test_Queue"))

      checkQueues(queueLogicService(), resubmission, lta, calculationResponse, expectedDmsQueues)
    }

    "MiniRegime must be included when scheme has credit" in {

      val resubmission = false

      val lta = None

      val calculationResponse: CalculationResponse = calculationResponseWith(
        directCompensation = 0,
        indirectCompensation = 0,
        memberCredit = 0,
        schemeCredit = 1,
        debit = 0
      )

      val expectedDmsQueues = Seq(MiniRegime("Test_Queue"))

      checkQueues(queueLogicService(), resubmission, lta, calculationResponse, expectedDmsQueues)
    }

    "MiniRegime must be included when calculation is in debit" in {

      val resubmission = false

      val lta = None

      val calculationResponse: CalculationResponse = calculationResponseWith(
        directCompensation = 0,
        indirectCompensation = 0,
        memberCredit = 0,
        schemeCredit = 0,
        debit = 1
      )

      val expectedDmsQueues = Seq(MiniRegime("Test_Queue"))

      checkQueues(queueLogicService(), resubmission, lta, calculationResponse, expectedDmsQueues)
    }

    "MiniRegimeAmendment must be included when member has credit and is resubmission" in {

      val resubmission = true

      val lta = None

      val calculationResponse: CalculationResponse = calculationResponseWith(
        directCompensation = 0,
        indirectCompensation = 0,
        memberCredit = 1,
        schemeCredit = 0,
        debit = 0
      )

      val expectedDmsQueues = Seq(MiniRegimeAmendment("Test_Queue"))

      checkQueues(queueLogicService(), resubmission, lta, calculationResponse, expectedDmsQueues)
    }

    "MiniRegimeAmendment must be included when scheme has credit and is resubmission" in {

      val resubmission = true

      val lta = None

      val calculationResponse: CalculationResponse = calculationResponseWith(
        directCompensation = 0,
        indirectCompensation = 0,
        memberCredit = 0,
        schemeCredit = 1,
        debit = 0
      )

      val expectedDmsQueues = Seq(MiniRegimeAmendment("Test_Queue"))

      checkQueues(queueLogicService(), resubmission, lta, calculationResponse, expectedDmsQueues)
    }

    "MiniRegimeAmendment must be included when calculation is in debit and is resubmission" in {

      val resubmission = true

      val lta = None

      val calculationResponse: CalculationResponse = calculationResponseWith(
        directCompensation = 0,
        indirectCompensation = 0,
        memberCredit = 0,
        schemeCredit = 0,
        debit = 1
      )

      val expectedDmsQueues = Seq(MiniRegimeAmendment("Test_Queue"))

      checkQueues(queueLogicService(), resubmission, lta, calculationResponse, expectedDmsQueues)
    }

    "Compensation, MiniRegime and LTA must be included when calculation is in credit and compensation is specified and lta details exist" in {

      val resubmission = false

      val lta = Some(TestData.lifeTimeAllowance)

      val calculationResponse: CalculationResponse = calculationResponseWith(
        directCompensation = 1,
        indirectCompensation = 1,
        memberCredit = 1,
        schemeCredit = 1,
        debit = 1
      )

      val expectedDmsQueues = Seq(Compensation("Test_Queue"), MiniRegime("Test_Queue"), LTA("Test_Queue"))

      checkQueues(queueLogicService(), resubmission, lta, calculationResponse, expectedDmsQueues)
    }

  }

  private def queueLogicService() = {
    val service = new QueueLogicService(
      mockConfiguration,
      mockSubmissionReferenceService
    )
    service
  }

  private def checkQueues(
    service: QueueLogicService,
    resubmission: Boolean,
    lta: Option[LifeTimeAllowance],
    calculationResponse: CalculationResponse,
    expectedDmsQueues: Seq[DmsQueue]
  ) = {
    val calculationInputs = CalculationInputs(
      InputsResubmission(resubmission, None),
      Setup(
        Some(
          AnnualAllowanceSetup(
            Some(true),
            Some(false),
            Some(false),
            Some(false),
            Some(false),
            Some(false),
            Some(MaybePIAIncrease.No),
            Some(MaybePIAUnchangedOrDecreased.No),
            Some(false),
            Some(false),
            Some(false),
            Some(false)
          )
        ),
        Some(
          LifetimeAllowanceSetup(
            Some(true),
            Some(false),
            Some(true),
            Some(false),
            Some(false),
            Some(false),
            Some(true)
          )
        )
      ),
      None,
      lta
    )

    val finalSubmission                      = FinalSubmission(calculationInputs, Some(calculationResponse), submissionInputs)
    val queueReferences: Seq[QueueReference] = service.computeQueueReferences(finalSubmission)

    val dmsQueues: Seq[DmsQueue] = queueReferences.map(qr => qr.dmsQueue)

    dmsQueues mustBe expectedDmsQueues
  }

  private def calculationResponseWith(
    directCompensation: Int,
    indirectCompensation: Int,
    memberCredit: Int,
    schemeCredit: Int,
    debit: Int
  ) = {
    val calculationResponse = CalculationResponse(
      Resubmission(false, None),
      TotalAmounts(1000, 0, 0),
      List(
        OutOfDatesTaxYearsCalculation(
          period = Period._2018,
          directCompensation = directCompensation,
          indirectCompensation = indirectCompensation,
          chargePaidByMember = 0,
          chargePaidBySchemes = 0,
          revisedChargableAmountBeforeTaxRate = 0,
          revisedChargableAmountAfterTaxRate = 0,
          unusedAnnualAllowance = 0,
          taxYearSchemes = List.empty,
          adjustedCompensation = Some(0)
        )
      ),
      List(
        InDatesTaxYearsCalculation(
          period = Period._2018,
          memberCredit = memberCredit,
          schemeCredit = schemeCredit,
          debit = debit,
          chargePaidByMember = 0,
          chargePaidBySchemes = 0,
          revisedChargableAmountBeforeTaxRate = 0,
          revisedChargableAmountAfterTaxRate = 0,
          unusedAnnualAllowance = 0,
          taxYearSchemes = List.empty,
          totalCompensation = Some(0)
        )
      )
    )
    calculationResponse
  }
}
