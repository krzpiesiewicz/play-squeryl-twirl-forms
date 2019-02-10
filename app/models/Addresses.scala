package models

import com.iterable.play.utils.CaseClassMapping

import play.api.data._
import play.api.data.validation._
import play.api.data.Forms._
import play.api.data.FormError
import play.api.data.Mapping

import forms.NonEmptyString

sealed trait Address {
  val location: Location
}

case class UrbanAddress(
  country: String,
  state: String,
  city: String,
  street: String,
  houseNumber: String,
  flatNumber: Option[Int],
  override val location: Location) extends Address {
}

object UrbanAddress {
  implicit val mapping = play.api.data.Forms.mapping(
    "country" -> nonEmptyText,
    "state" -> nonEmptyText,
    "city" -> nonEmptyText,
    "street" -> nonEmptyText,
    "houseNumber" -> nonEmptyText,
    "flatNumber" -> optional(number),
    "location" -> Location.mapping)(UrbanAddress.apply)(UrbanAddress.unapply)
}

case class CountrysideAddress(
  country: NonEmptyString,
  state: NonEmptyString,
  county: NonEmptyString,
  houseNumber: NonEmptyString,
  override val location: Location) extends Address

object CountrysideAddress {
  implicit val mapping = CaseClassMapping.mapping[CountrysideAddress]
}