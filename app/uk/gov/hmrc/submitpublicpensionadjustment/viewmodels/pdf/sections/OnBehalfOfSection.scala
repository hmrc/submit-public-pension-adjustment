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

import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.{FinalSubmission, OnBehalfOfMember, PersonalDetails}
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.{Formatting, Section}

case class OnBehalfOfSection(
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
  ninoOrTrn: String
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
    "ninoOrTrn"
  )
}
object OnBehalfOfSection extends Formatting {

  def build(finalSubmission: FinalSubmission): Option[OnBehalfOfSection] =
    finalSubmission.submissionInputs.administrativeDetails.onBehalfOfMember.map { memberDetails =>
      OnBehalfOfSection(
        firstName = firstName(memberDetails),
        surname = surname(memberDetails),
        dob = dob(memberDetails),
        organisation = organisation(memberDetails),
        addressLine1 = addressLine1(memberDetails),
        addressLine2 = addressLine2(memberDetails),
        addressLine3 = addressLine3(memberDetails),
        townOrCity = townOrCity(memberDetails),
        county = county(memberDetails),
        stateOrRegion = stateOrRegion(memberDetails),
        postCode = postCode(memberDetails),
        postalCode = postalCode(memberDetails),
        country = country(memberDetails),
        utr = utr(memberDetails),
        ninoOrTrn = ninoOrTrn(memberDetails)
      )
    }

  private def firstName(onBehalfOfMember: OnBehalfOfMember) =
    nameParts(onBehalfOfMember).dropRight(1).mkString(" ")

  private def surname(onBehalfOfMember: OnBehalfOfMember) =
    nameParts(onBehalfOfMember).last

  private def nameParts(onBehalfOfMember: OnBehalfOfMember) =
    onBehalfOfMember.memberPersonalDetails.fullName.split(" ")

  private def dob(onBehalfOfMember: OnBehalfOfMember) = {
    val dobOption = onBehalfOfMember.memberPersonalDetails.dateOfBirth
    dobOption
      .map(dob => format(dob))
      .getOrElse(NotEntered)
  }

  private def organisation(onBehalfOfMember: OnBehalfOfMember): Option[String] =
    onBehalfOfMember.memberPersonalDetails match {
      case PersonalDetails(_, _, _, _, _, Some(address), None, _)              => address.organisation
      case PersonalDetails(_, _, _, _, _, None, Some(internationalAddress), _) =>
        internationalAddress.organisation
      case _                                                                   => Some(NotEntered)
    }

  private def addressLine1(onBehalfOfMember: OnBehalfOfMember): String =
    onBehalfOfMember.memberPersonalDetails match {
      case PersonalDetails(_, _, _, _, _, Some(address), None, _)              => address.addressLine1
      case PersonalDetails(_, _, _, _, _, None, Some(internationalAddress), _) => internationalAddress.addressLine1
      case _                                                                   => NotEntered
    }

  private def addressLine2(onBehalfOfMember: OnBehalfOfMember): String =
    onBehalfOfMember.memberPersonalDetails match {
      case PersonalDetails(_, _, _, _, _, Some(address), None, _)              => address.addressLine2.getOrElse(NotEntered)
      case PersonalDetails(_, _, _, _, _, None, Some(internationalAddress), _) =>
        internationalAddress.addressLine2.getOrElse(NotEntered)
      case _                                                                   => NotEntered
    }

  private def addressLine3(onBehalfOfMember: OnBehalfOfMember): String =
    onBehalfOfMember.memberPersonalDetails match {
      case PersonalDetails(_, _, _, _, _, Some(address), None, _)              => address.addressLine3.getOrElse(NotEntered)
      case PersonalDetails(_, _, _, _, _, None, Some(internationalAddress), _) =>
        internationalAddress.addressLine3.getOrElse(NotEntered)
      case _                                                                   => NotEntered
    }

  private def townOrCity(onBehalfOfMember: OnBehalfOfMember): String =
    onBehalfOfMember.memberPersonalDetails match {
      case PersonalDetails(_, _, _, _, _, Some(address), None, _)              => address.townOrCity
      case PersonalDetails(_, _, _, _, _, None, Some(internationalAddress), _) =>
        internationalAddress.townOrCity
      case _                                                                   => NotEntered
    }

  private def county(onBehalfOfMember: OnBehalfOfMember): Option[String] =
    onBehalfOfMember.memberPersonalDetails match {
      case PersonalDetails(_, _, _, _, _, Some(address), None, _) => address.county
      case PersonalDetails(_, _, _, _, _, None, Some(_), _)       =>
        None
      case _                                                      => Some(NotEntered)
    }

  private def stateOrRegion(onBehalfOfMember: OnBehalfOfMember): Option[String] =
    onBehalfOfMember.memberPersonalDetails match {
      case PersonalDetails(_, _, _, _, _, Some(_), None, _)                    => None
      case PersonalDetails(_, _, _, _, _, None, Some(internationalAddress), _) =>
        internationalAddress.stateOrRegion
      case _                                                                   => Some(NotEntered)
    }

  private def postCode(onBehalfOfMember: OnBehalfOfMember): Option[String] =
    onBehalfOfMember.memberPersonalDetails match {
      case PersonalDetails(_, _, _, _, _, Some(address), None, _) => Some(address.postCode.getOrElse(NotEntered))
      case PersonalDetails(_, _, _, _, _, None, Some(_), _)       => None
      case _                                                      => Some(NotEntered)
    }

  private def postalCode(onBehalfOfMember: OnBehalfOfMember): Option[String] =
    onBehalfOfMember.memberPersonalDetails match {
      case PersonalDetails(_, _, _, _, _, Some(_), None, _)                    => None
      case PersonalDetails(_, _, _, _, _, None, Some(internationalAddress), _) =>
        internationalAddress.postCode
      case _                                                                   => Some(NotEntered)
    }
  private def country(onBehalfOfMember: OnBehalfOfMember): String            =
    onBehalfOfMember.memberPersonalDetails match {
      case PersonalDetails(_, _, _, _, _, Some(_), None, _)                    => UnitedKingdom
      case PersonalDetails(_, _, _, _, _, None, Some(internationalAddress), _) => internationalAddress.country
      case _                                                                   => UnitedKingdom
    }

  private def utr(onBehalfOfMember: OnBehalfOfMember): String =
    formatString(onBehalfOfMember.taxIdentifiers.utr)

  private def ninoOrTrn(onBehalfOfMember: OnBehalfOfMember): String =
    onBehalfOfMember.taxIdentifiers.nino.getOrElse(formatString(onBehalfOfMember.taxIdentifiers.trn))

}
