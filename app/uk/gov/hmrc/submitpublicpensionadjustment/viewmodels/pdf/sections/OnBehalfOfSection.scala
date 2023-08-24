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

import uk.gov.hmrc.submitpublicpensionadjustment.models.finalsubmission.{FinalSubmission, OnBehalfOfMember, PersonalDetails}
import uk.gov.hmrc.submitpublicpensionadjustment.viewmodels.pdf.Section

import java.time.ZoneId
import java.time.format.DateTimeFormatter

case class OnBehalfOfSection(
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
  ninoOrTrn: String
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
    "ninoOrTrn"
  )
}
object OnBehalfOfSection {

  def build(finalSubmission: FinalSubmission): Option[OnBehalfOfSection] =
    finalSubmission.submissionInputs.administrativeDetails.onBehalfOfMember.map { memberDetails =>
      OnBehalfOfSection(
        firstName = firstName(memberDetails),
        surname = surname(memberDetails),
        dob = dob(memberDetails),
        addressLine1 = addressLine1(memberDetails),
        addressLine2 = addressLine2(memberDetails),
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
      .map(dob => DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.systemDefault()).format(dob))
      .getOrElse("Not Entered")
  }

  private def addressLine1(onBehalfOfMember: OnBehalfOfMember): String =
    onBehalfOfMember.memberPersonalDetails match {
      case PersonalDetails(_, _, _, _, _, Some(address), None, _)              => address.addressLine1
      case PersonalDetails(_, _, _, _, _, None, Some(internationalAddress), _) => internationalAddress.addressLine1
      case _                                                                   => "Not Entered"
    }

  private def addressLine2(onBehalfOfMember: OnBehalfOfMember): String =
    onBehalfOfMember.memberPersonalDetails match {
      case PersonalDetails(_, _, _, _, _, Some(address), None, _)              => address.addressLine2.getOrElse("Not Entered")
      case PersonalDetails(_, _, _, _, _, None, Some(internationalAddress), _) =>
        internationalAddress.addressLine2.getOrElse("Not Entered")
      case _                                                                   => "Not Entered"
    }

  private def townOrCity(onBehalfOfMember: OnBehalfOfMember): String =
    onBehalfOfMember.memberPersonalDetails match {
      case PersonalDetails(_, _, _, _, _, Some(address), None, _)              => address.townOrCity
      case PersonalDetails(_, _, _, _, _, None, Some(internationalAddress), _) =>
        internationalAddress.townOrCity
      case _                                                                   => "Not Entered"
    }

  private def county(onBehalfOfMember: OnBehalfOfMember): Option[String] =
    onBehalfOfMember.memberPersonalDetails match {
      case PersonalDetails(_, _, _, _, _, Some(address), None, _) => address.county
      case PersonalDetails(_, _, _, _, _, None, Some(_), _)       =>
        None
      case _                                                      => Some("Not Entered")
    }

  private def stateOrRegion(onBehalfOfMember: OnBehalfOfMember): Option[String] =
    onBehalfOfMember.memberPersonalDetails match {
      case PersonalDetails(_, _, _, _, _, Some(_), None, _)                    => None
      case PersonalDetails(_, _, _, _, _, None, Some(internationalAddress), _) =>
        internationalAddress.stateOrRegion
      case _                                                                   => Some("Not Entered")
    }

  private def postCode(onBehalfOfMember: OnBehalfOfMember): Option[String] =
    onBehalfOfMember.memberPersonalDetails match {
      case PersonalDetails(_, _, _, _, _, Some(address), None, _) => Some(address.postCode)
      case PersonalDetails(_, _, _, _, _, None, Some(_), _)       => None
      case _                                                      => Some("Not Entered")
    }

  private def postalCode(onBehalfOfMember: OnBehalfOfMember): Option[String] =
    onBehalfOfMember.memberPersonalDetails match {
      case PersonalDetails(_, _, _, _, _, Some(_), None, _)                    => None
      case PersonalDetails(_, _, _, _, _, None, Some(internationalAddress), _) =>
        internationalAddress.postCode
      case _                                                                   => Some("Not Entered")
    }
  private def country(onBehalfOfMember: OnBehalfOfMember): String            =
    onBehalfOfMember.memberPersonalDetails match {
      case PersonalDetails(_, _, _, _, _, Some(_), None, _)                    => "United Kingdom"
      case PersonalDetails(_, _, _, _, _, None, Some(internationalAddress), _) => internationalAddress.country
      case _                                                                   => "United Kingdom"
    }

  private def utr(onBehalfOfMember: OnBehalfOfMember): String = {
    val utr = onBehalfOfMember.taxIdentifiers.utr.getOrElse("Not Entered")
    if (utr == "") "Not Entered" else utr
  }

  private def ninoOrTrn(onBehalfOfMember: OnBehalfOfMember): String =
    onBehalfOfMember.taxIdentifiers.nino.getOrElse(onBehalfOfMember.taxIdentifiers.trn.getOrElse("Not Entered"))

}
