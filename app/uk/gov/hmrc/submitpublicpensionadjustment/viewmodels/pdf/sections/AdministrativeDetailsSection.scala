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

package uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.sections

import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.FinalSubmission
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.Section

case class AdministrativeDetailsSection(
  firstName: String,
  surname: String,
  dob: String,
  addressLine1: String,
  addressLine2: String,
  postCode: String,
  country: Option[String],
  utr: Option[String],
  ninoOrTrn: String,
  contactNumber: String
) extends Section {

  override def orderedFieldNames(): Seq[String] = Seq(
    "firstName",
    "surname",
    "dob",
    "addressLine1",
    "addressLine2",
    "postCode",
    "country",
    "utr",
    "ninoOrTrn",
    "contactNumber"
  )
}

object AdministrativeDetailsSection {

  // TODO - Need to map values from final submission.
  def build(finalSubmission: FinalSubmission): AdministrativeDetailsSection =
    AdministrativeDetailsSection(
      firstName = firstName(finalSubmission),
      surname = surname(finalSubmission),
      dob = "dob",
      addressLine1 = "addressLine1",
      addressLine2 = "addressLine2",
      postCode = "postCode",
      country = Some("country"),
      utr = Some("utr"),
      ninoOrTrn = "ninoOrTrn",
      contactNumber = "contactNumber"
    )

  private def firstName(finalSubmission: FinalSubmission) =
    nameParts(finalSubmission).dropRight(1).mkString(" ")

  private def surname(finalSubmission: FinalSubmission) =
    nameParts(finalSubmission).last

  private def nameParts(finalSubmission: FinalSubmission) =
    finalSubmission.submissionInputs.administrativeDetails.claimantDetails.claimantPersonalDetails.fullName.split(" ")
}
