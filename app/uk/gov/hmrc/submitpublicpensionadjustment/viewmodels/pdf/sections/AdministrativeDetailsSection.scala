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

import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.{FinalSubmission, PersonalDetails}
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.{Formatting, Section}

case class AdministrativeDetailsSection(
  firstName: String,
  surname: String,
  dob: String,
  organisation: Option[String],
  addressLine1: String,
  addressLine2: String,
  addressLine3: String,
  townOrCity: String,
  county: Option[String],
  stateOrRegion: Option[String],
  postCode: Option[String],
  postalCode: Option[String],
  country: String,
  utr: String,
  ninoOrTrn: String,
  contactNumber: String,
  resubmissionReason: Option[String]
) extends Section {

  override def orderedFieldNames(): Seq[String] = Seq(
    "firstName",
    "surname",
    "dob",
    "organisation",
    "addressLine1",
    "addressLine2",
    "addressLine3",
    "townOrCity",
    "county",
    "stateOrRegion",
    "postCode",
    "postalCode",
    "country",
    "utr",
    "ninoOrTrn",
    "contactNumber",
    "resubmissionReason"
  )
}
object AdministrativeDetailsSection extends Formatting {

  def build(finalSubmission: FinalSubmission): AdministrativeDetailsSection =
    AdministrativeDetailsSection(
      firstName = firstName(finalSubmission),
      surname = surname(finalSubmission),
      dob = dob(finalSubmission),
      organisation = organisation(finalSubmission),
      addressLine1 = addressLine1(finalSubmission),
      addressLine2 = addressLine2(finalSubmission),
      addressLine3 = addressLine3(finalSubmission),
      townOrCity = townOrCity(finalSubmission),
      county = county(finalSubmission),
      stateOrRegion = stateOrRegion(finalSubmission),
      postCode = postCode(finalSubmission),
      postalCode = postalCode(finalSubmission),
      country = country(finalSubmission),
      utr = utr(finalSubmission),
      ninoOrTrn = ninoOrTrn(finalSubmission),
      contactNumber = contactNumber(finalSubmission),
      resubmissionReason = resubmissionReason(finalSubmission)
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
      .map(dob => format(dob))
      .getOrElse(NotEntered)
  }

  private def organisation(finalSubmission: FinalSubmission): Option[String] =
    finalSubmission.submissionInputs.administrativeDetails.claimantDetails.claimantPersonalDetails match {
      case PersonalDetails(_, _, _, Some(address), None, _, _, _)              => address.organisation
      case PersonalDetails(_, _, _, None, Some(internationalAddress), _, _, _) =>
        internationalAddress.organisation
      case _                                                                   => Some(NotEntered)
    }

  private def addressLine1(finalSubmission: FinalSubmission): String =
    finalSubmission.submissionInputs.administrativeDetails.claimantDetails.claimantPersonalDetails match {
      case PersonalDetails(_, _, _, Some(address), None, _, _, _)              => address.addressLine1
      case PersonalDetails(_, _, _, None, Some(internationalAddress), _, _, _) => internationalAddress.addressLine1
      case _                                                                   => NotEntered
    }

  private def addressLine2(finalSubmission: FinalSubmission): String =
    finalSubmission.submissionInputs.administrativeDetails.claimantDetails.claimantPersonalDetails match {
      case PersonalDetails(_, _, _, Some(address), None, _, _, _)              => address.addressLine2.getOrElse(NotEntered)
      case PersonalDetails(_, _, _, None, Some(internationalAddress), _, _, _) =>
        internationalAddress.addressLine2.getOrElse(NotEntered)
      case _                                                                   => NotEntered
    }

  private def addressLine3(finalSubmission: FinalSubmission): String =
    finalSubmission.submissionInputs.administrativeDetails.claimantDetails.claimantPersonalDetails match {
      case PersonalDetails(_, _, _, Some(address), None, _, _, _)              => address.addressLine3.getOrElse(NotEntered)
      case PersonalDetails(_, _, _, None, Some(internationalAddress), _, _, _) =>
        internationalAddress.addressLine3.getOrElse(NotEntered)
      case _                                                                   => NotEntered
    }

  private def townOrCity(finalSubmission: FinalSubmission): String =
    finalSubmission.submissionInputs.administrativeDetails.claimantDetails.claimantPersonalDetails match {
      case PersonalDetails(_, _, _, Some(address), None, _, _, _)              => address.townOrCity
      case PersonalDetails(_, _, _, None, Some(internationalAddress), _, _, _) =>
        internationalAddress.townOrCity
      case _                                                                   => NotEntered
    }

  private def county(finalSubmission: FinalSubmission): Option[String] =
    finalSubmission.submissionInputs.administrativeDetails.claimantDetails.claimantPersonalDetails match {
      case PersonalDetails(_, _, _, Some(address), None, _, _, _) => address.county
      case PersonalDetails(_, _, _, None, Some(_), _, _, _)       =>
        None
      case _                                                      => Some(NotEntered)
    }

  private def stateOrRegion(finalSubmission: FinalSubmission): Option[String] =
    finalSubmission.submissionInputs.administrativeDetails.claimantDetails.claimantPersonalDetails match {
      case PersonalDetails(_, _, _, Some(_), None, _, _, _)                    => None
      case PersonalDetails(_, _, _, None, Some(internationalAddress), _, _, _) =>
        internationalAddress.stateOrRegion
      case _                                                                   => Some(NotEntered)
    }

  private def postCode(finalSubmission: FinalSubmission): Option[String] =
    finalSubmission.submissionInputs.administrativeDetails.claimantDetails.claimantPersonalDetails match {
      case PersonalDetails(_, _, _, Some(address), None, _, _, _) => Some(address.postCode.getOrElse(NotEntered))
      case PersonalDetails(_, _, _, None, Some(_), _, _, _)       => None
      case _                                                      => Some(NotEntered)
    }

  private def postalCode(finalSubmission: FinalSubmission): Option[String] =
    finalSubmission.submissionInputs.administrativeDetails.claimantDetails.claimantPersonalDetails match {
      case PersonalDetails(_, _, _, Some(_), None, _, _, _)                    => None
      case PersonalDetails(_, _, _, None, Some(internationalAddress), _, _, _) =>
        internationalAddress.postCode
      case _                                                                   => Some(NotEntered)
    }

  private def country(finalSubmission: FinalSubmission): String =
    finalSubmission.submissionInputs.administrativeDetails.claimantDetails.claimantPersonalDetails match {
      case PersonalDetails(_, _, _, Some(_), None, _, _, _)                    => UnitedKingdom
      case PersonalDetails(_, _, _, None, Some(internationalAddress), _, _, _) => internationalAddress.country
      case _                                                                   => UnitedKingdom
    }

  private def utr(finalSubmission: FinalSubmission): String =
    formatString(finalSubmission.submissionInputs.administrativeDetails.claimantDetails.taxIdentifiers.utr)

  private def ninoOrTrn(finalSubmission: FinalSubmission): String =
    formatString(finalSubmission.submissionInputs.administrativeDetails.claimantDetails.taxIdentifiers.nino)

  private def contactNumber(finalSubmission: FinalSubmission): String =
    formatString(
      finalSubmission.submissionInputs.administrativeDetails.claimantDetails.claimantPersonalDetails.contactPhoneNumber
    )

  private def resubmissionReason(finalSubmission: FinalSubmission): Option[String] =
    finalSubmission.calculationInputs.resubmission.reason
}
