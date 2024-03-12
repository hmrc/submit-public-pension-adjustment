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

//package base
//
//import com.github.tomakehurst.wiremock.http.Response.response
//import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
//import org.scalatest.freespec.AnyFreeSpec
//import org.scalatest.matchers.must.Matchers
//import org.scalatest.{OptionValues, TryValues}
//import play.api.Application
//import play.api.i18n.{Messages, MessagesApi}
//import play.api.inject.bind
//import play.api.inject.guice.GuiceApplicationBuilder
//import play.api.test.FakeRequest
//import uk.gov.hmrc.submitpublicpensionadjustment.controllers.actions.IdentifierAction
//import uk.gov.hmrc.submitpublicpensionadjustment.models.UserAnswers
//import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.TaxYear2016To2023.NormalTaxYear
//import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.inputs.{AnnualAllowance, CalculationInputs, Resubmission, Period => InputsPeriod}
//import uk.gov.hmrc.submitpublicpensionadjustment.models.calculation.response.{CalculationResponse, InDatesTaxYearsCalculation, Period, TaxYearScheme, TotalAmounts, Resubmission => ResponseResubmission}
//import uk.gov.hmrc.submitpublicpensionadjustment.models.submission.Submission
//
//import java.time.{Clock, Instant, LocalDate, ZoneId}
//
//trait SpecBase
//    extends AnyFreeSpec
//    with Matchers
//    with TryValues
//    with OptionValues
//    with ScalaFutures
//    with IntegrationPatience {
//
//  val userAnswersId: String = "id"
//
//  def aCalculationResponseWithAnInDateDebitYear = {
//
//    val inDatesYears = List(
//      inDatesTaxYearsCalculation(Period._2023, 0),
//      inDatesTaxYearsCalculation(Period._2022, 1),
//      inDatesTaxYearsCalculation(Period._2020, 1),
//      inDatesTaxYearsCalculation(Period._2021, 1)
//    )
//
//    val calculationResponse = CalculationResponse(
//      ResponseResubmission(false, None),
//      TotalAmounts(0, 1, 0),
//      List.empty,
//      inDatesYears
//    )
//    calculationResponse
//  }
//
//  private def inDatesTaxYearsCalculation(period1: Period, debitAmount: Int) =
//    InDatesTaxYearsCalculation(
//      period = period1,
//      memberCredit = 0,
//      schemeCredit = 0,
//      debit = debitAmount,
//      chargePaidByMember = 0,
//      chargePaidBySchemes = 0,
//      revisedChargableAmountBeforeTaxRate = 0,
//      revisedChargableAmountAfterTaxRate = 0,
//      unusedAnnualAllowance = 0,
//      taxYearSchemes = List.empty
//    )
//
//  val sessionId: String = "sessionId"
//
//  val uniqueId: String = "uniqueId"
//
//  def emptyUserAnswers: UserAnswers = UserAnswers(userAnswersId)
//
//  val resubmission = Resubmission(false, None)
//
//  val calculationInputs = CalculationInputs(resubmission, None, None)
//
//  val submission = Submission(sessionId, uniqueId, calculationInputs, None)
//
//  def submissionRelatingToTaxYearSchemes(taxYearSchemes: List[TaxYearScheme]): Submission = {
//    val resubmission      = Resubmission(false, None)
//    val annualAllowance   = AnnualAllowance(
//      List.empty,
//      List(NormalTaxYear(0, taxYearSchemes, 0, 0, InputsPeriod._2017, None))
//    )
//    val calculationInputs = CalculationInputs(resubmission, Some(annualAllowance), None)
//    Submission(sessionId, uniqueId, calculationInputs, None)
//  }
//
//  protected val fixedInstant: Instant      = LocalDate.now.atStartOfDay(ZoneId.systemDefault).toInstant
//  protected val clockAtFixedInstant: Clock = Clock.fixed(fixedInstant, ZoneId.systemDefault)
//
//  def messages(app: Application): Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())
//
//  protected def applicationBuilder(
//    userAnswers: Option[UserAnswers] = None,
//    submission: Option[Submission] = None
//  ): GuiceApplicationBuilder =
//    new GuiceApplicationBuilder()
//      .overrides(
//        bind[DataRequiredAction].to[DataRequiredActionImpl],
//        bind[CalculationDataRequiredAction].to[CalculationDataRequiredActionImpl],
//        bind[IdentifierAction].to[FakeIdentifierAction],
//        bind[LandingPageIdentifierAction].to[FakeLandingPageIdentifierAction],
//        bind[DataRetrievalAction].toInstance(new FakeDataRetrievalAction(userAnswers, submission))
//      )
//}
