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
  townOrCity: String,
  county: Option[String],
  stateOrRegion: Option[String],
  postCode: Option[String],
  postalCode: Option[String],
  country: String,
  utr: String,
  ninoOrTrn: String,
  contactNumber: String
) extends Section {

  override def orderedFieldNames(): Seq[String] = Seq(
    "firstName",
    "surname",
    "dob",
    "addressLine1",
    "addressLine2",
    "townOrCity",
    "county",
    "stateOrRegion",
    "postCode",
    "postalCode",
    "country",
    "utr",
    "ninoOrTrn",
    "contactNumber"
  )
}
object AdministrativeDetailsSection {

  def build(finalSubmission: FinalSubmission): AdministrativeDetailsSection =
    AdministrativeDetailsSection(
      firstName = firstName(finalSubmission),
      surname = surname(finalSubmission),
      dob = dob(finalSubmission),
      addressLine1 = addressLine1(finalSubmission),
      addressLine2 = addressLine2(finalSubmission),
      townOrCity = townOrCity(finalSubmission),
      county = county(finalSubmission),
      stateOrRegion = stateOrRegion(finalSubmission),
      postCode = postCode(finalSubmission),
      postalCode = postalCode(finalSubmission),
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
      case _                                                             => "Not Entered"
    }

  private def addressLine2(finalSubmission: FinalSubmission): String =
    finalSubmission.submissionInputs.administrativeDetails.claimantDetails.claimantPersonalDetails match {
      case PersonalDetails(_, _, _, Some(address), None, _)              => address.addressLine2.getOrElse("Not Entered")
      case PersonalDetails(_, _, _, None, Some(internationalAddress), _) =>
        internationalAddress.addressLine2.getOrElse("Not Entered")
      case _                                                             => "Not Entered"
    }

  private def townOrCity(finalSubmission: FinalSubmission): String =
    finalSubmission.submissionInputs.administrativeDetails.claimantDetails.claimantPersonalDetails match {
      case PersonalDetails(_, _, _, Some(address), None, _)              => address.townOrCity
      case PersonalDetails(_, _, _, None, Some(internationalAddress), _) =>
        internationalAddress.townOrCity
      case _                                                             => "Not Entered"
    }

  private def county(finalSubmission: FinalSubmission): Option[String] =
    finalSubmission.submissionInputs.administrativeDetails.claimantDetails.claimantPersonalDetails match {
      case PersonalDetails(_, _, _, Some(address), None, _) => address.county
      case PersonalDetails(_, _, _, None, Some(_), _)       =>
        None
      case _                                                => Some("Not Entered")
    }

  private def stateOrRegion(finalSubmission: FinalSubmission): Option[String] =
    finalSubmission.submissionInputs.administrativeDetails.claimantDetails.claimantPersonalDetails match {
      case PersonalDetails(_, _, _, Some(_), None, _)                    => None
      case PersonalDetails(_, _, _, None, Some(internationalAddress), _) =>
        internationalAddress.stateOrRegion
      case _                                                             => Some("Not Entered")
    }

  private def postCode(finalSubmission: FinalSubmission): Option[String] =
    finalSubmission.submissionInputs.administrativeDetails.claimantDetails.claimantPersonalDetails match {
      case PersonalDetails(_, _, _, Some(address), None, _) => Some(address.postCode)
      case PersonalDetails(_, _, _, None, Some(_), _)       => None
      case _                                                => Some("Not Entered")
    }

  private def postalCode(finalSubmission: FinalSubmission): Option[String] =
    finalSubmission.submissionInputs.administrativeDetails.claimantDetails.claimantPersonalDetails match {
      case PersonalDetails(_, _, _, Some(_), None, _)                    => None
      case PersonalDetails(_, _, _, None, Some(internationalAddress), _) =>
        internationalAddress.postCode
      case _                                                             => Some("Not Entered")
    }

  private def country(finalSubmission: FinalSubmission): String =
    finalSubmission.submissionInputs.administrativeDetails.claimantDetails.claimantPersonalDetails match {
      case PersonalDetails(_, _, _, Some(_), None, _)                    => "United Kingdom"
      case PersonalDetails(_, _, _, None, Some(internationalAddress), _) => internationalAddress.country
      case _                                                             => "United Kingdom"
    }

  private def utr(finalSubmission: FinalSubmission): String =
    finalSubmission.submissionInputs.administrativeDetails.claimantDetails.taxIdentifiers.utr.getOrElse("Not Entered")

  private def ninoOrTrn(finalSubmission: FinalSubmission): String = {
    val taxIds = finalSubmission.submissionInputs.administrativeDetails.claimantDetails.taxIdentifiers
    taxIds.nino.getOrElse("Not Entered")
  }

  private def contactNumber(finalSubmission: FinalSubmission): String = {
    val number =
      finalSubmission.submissionInputs.administrativeDetails.claimantDetails.claimantPersonalDetails.contactPhoneNumber
        .getOrElse("Not Entered")

    if (number == "") "Not Entered" else number
  }

}
