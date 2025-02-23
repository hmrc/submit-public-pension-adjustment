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

package uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.sections

import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.FinalSubmission
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.Section

case class PaymentInformationSection(
  accountName: String,
  sortCode: String,
  accountNumber: String,
  rollNumber: Option[String]
) extends Section {
  override def orderedFieldNames(): Seq[String] = Seq("accountName", "sortCode", "accountNumber", "rollNumber")
}

object PaymentInformationSection {

  def build(finalSubmission: FinalSubmission): Option[PaymentInformationSection] =
    finalSubmission.submissionInputs.bankAccountDetails.map { bankDetails =>
      PaymentInformationSection(
        accountName = bankDetails.accountName,
        sortCode = bankDetails.sortCode,
        accountNumber = bankDetails.accountNumber,
        rollNumber = bankDetails.rollNumber
      )
    }
}
