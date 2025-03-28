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

package uk.gov.hmrc.submitpublicpensionadjustment.models.dms

import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.FinalSubmission

sealed trait DmsQueue {
  def isRequired(finalSubmission: FinalSubmission): Boolean
  def queueName: String
  def precedence: Int

  def isResubmission(finalSubmission: FinalSubmission): Boolean =
    finalSubmission.calculationInputs.resubmission.isResubmission

  def compensationIsRequired(finalSubmission: FinalSubmission): Boolean =
    finalSubmission.calculation match {
      case Some(calc) =>
        calc.outDates.exists(odCalc => odCalc.directCompensation > 0 || odCalc.indirectCompensation > 0)
      case None       => false
    }

  def miniRegimeIsRequired(finalSubmission: FinalSubmission): Boolean =
    finalSubmission.calculation match {
      case Some(calc) =>
        calc.inDates.exists(idCalc => idCalc.memberCredit > 0 || idCalc.schemeCredit > 0 || idCalc.debit > 0)
      case None       => false
    }
}

case class MiniRegime(queueName: String) extends DmsQueue {
  override def isRequired(finalSubmission: FinalSubmission): Boolean = if (isResubmission(finalSubmission)) {
    false
  } else {
    miniRegimeIsRequired(finalSubmission)
  }

  override def precedence: Int = 1
}

case class MiniRegimeAmendment(queueName: String) extends DmsQueue {
  override def isRequired(finalSubmission: FinalSubmission): Boolean = if (isResubmission(finalSubmission)) {
    miniRegimeIsRequired(finalSubmission)
  } else {
    false
  }

  override def precedence: Int = 2
}

case class Compensation(queueName: String) extends DmsQueue {
  override def isRequired(finalSubmission: FinalSubmission): Boolean = if (isResubmission(finalSubmission)) {
    false
  } else {
    compensationIsRequired(finalSubmission)
  }

  override def precedence: Int = 3
}

case class CompensationAmendment(queueName: String) extends DmsQueue {
  override def isRequired(finalSubmission: FinalSubmission): Boolean = if (isResubmission(finalSubmission)) {
    compensationIsRequired(finalSubmission)
  } else {
    false
  }

  override def precedence: Int = 4
}

case class LTA(queueName: String) extends DmsQueue {
  override def isRequired(finalSubmission: FinalSubmission): Boolean =
    finalSubmission.calculationInputs.lifeTimeAllowance.isDefined

  override def precedence: Int = 5
}
