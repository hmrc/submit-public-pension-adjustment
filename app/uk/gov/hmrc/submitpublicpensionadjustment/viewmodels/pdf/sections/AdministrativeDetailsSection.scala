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

import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.{FinalSubmission, PersonalDetails}
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.Section

import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
//Not entered for everything not ententered
object AdministrativeDetailsSection {

  def build(finalSubmission: FinalSubmission): AdministrativeDetailsSection =
    AdministrativeDetailsSection(
      firstName = firstName(finalSubmission),
      surname = surname(finalSubmission),
      dob = dob(finalSubmission),
      addressLine1 = addressLine1(finalSubmission),
      addressLine2 = addressLine2(finalSubmission),
      postCode = postCode(finalSubmission),
      country = country(finalSubmission),
      utr = utr(finalSubmission),
      ninoOrTrn = ninoOrTrn(finalSubmission),
      contactNumber = contactNumber(finalSubmission)
    )

  private def firstName(finalSubmission: FinalSubmission) =
    nameParts(finalSubmission).dropRight(1).mkString(" ")

  private def surname(finalSubmission: FinalSubmission): String =
    nameParts(finalSubmission).last

  private def nameParts(finalSubmission: FinalSubmission): Array[String] =
    finalSubmission.submissionInputs.administrativeDetails.claimantDetails.claimantPersonalDetails.fullName.split(" ")

  private def dob(finalSubmission: FinalSubmission): String = {
    val dobOption =
      finalSubmission.submissionInputs.administrativeDetails.claimantDetails.claimantPersonalDetails.dateOfBirth
    dobOption
      .map(dob => DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.systemDefault()).format(dob))
      .getOrElse("Not Entered")
  }

  private def addressLine1(finalSubmission: FinalSubmission): String =
    finalSubmission.submissionInputs.administrativeDetails.claimantDetails.claimantPersonalDetails match {
      case PersonalDetails(_, _, _, Some(address), None, _)              => address.addressLine1
      case PersonalDetails(_, _, _, None, Some(internationalAddress), _) => internationalAddress.addressLine1
      case _                                                             => ""
    }

  private def addressLine2(finalSubmission: FinalSubmission): String =
    finalSubmission.submissionInputs.administrativeDetails.claimantDetails.claimantPersonalDetails match {
      case PersonalDetails(_, _, _, Some(address), None, _)              => address.addressLine2.getOrElse("")
      case PersonalDetails(_, _, _, None, Some(internationalAddress), _) =>
        internationalAddress.addressLine2.getOrElse("")
      case _                                                             => ""
    }

  private def postCode(finalSubmission: FinalSubmission): String =
    finalSubmission.submissionInputs.administrativeDetails.claimantDetails.claimantPersonalDetails match {
      case PersonalDetails(_, _, _, Some(address), None, _)              => address.postCode
      case PersonalDetails(_, _, _, None, Some(internationalAddress), _) => internationalAddress.postCode.getOrElse("")
      case _                                                             => ""
    } // postal code for international

  private def country(finalSubmission: FinalSubmission): Option[String] =
    finalSubmission.submissionInputs.administrativeDetails.claimantDetails.claimantPersonalDetails match {
      case PersonalDetails(_, _, _, Some(_), None, _)                    => Some("United Kingdom")
      case PersonalDetails(_, _, _, None, Some(internationalAddress), _) => Some(internationalAddress.country)
      case _                                                             => None
    }

  private def utr(finalSubmission: FinalSubmission): Option[String] =
    finalSubmission.submissionInputs.administrativeDetails.claimantDetails.taxIdentifiers.utr

  private def ninoOrTrn(finalSubmission: FinalSubmission): String = {
    val taxIds = finalSubmission.submissionInputs.administrativeDetails.claimantDetails.taxIdentifiers
    // Todo Check if this is correct
    taxIds.nino.getOrElse("Not entered")
  }

  private def contactNumber(finalSubmission: FinalSubmission): String =
    finalSubmission.submissionInputs.administrativeDetails.claimantDetails.claimantPersonalDetails.contactPhoneNumber
      .getOrElse("Not entered")

}
