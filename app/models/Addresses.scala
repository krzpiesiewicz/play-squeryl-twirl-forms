package models

import com.iterable.play.utils.CaseClassMapping
import play.api.data.validation._
import play.api.data.Mapping

case class Location(latitude: Double, longitude: Double) {
  
  def validate() {
    if (!(latitude >= -90 && latitude <= 90))
      throw new Exception("latitude val should be in interval [-90, 90].")
    if (!(longitude >= -180 && longitude < 180))
      throw new Exception("longitude val should be in interval [-180, 180).")
  }
}

object Location {
  val constraint: Constraint[Location] = Constraint("constraints.location")({
    location =>
      val errors = try {
        location.validate()
        Nil
      } catch {
        case e: Exception => Seq(ValidationError(e.getMessage))
      }
      if (errors.isEmpty)
        Valid
      else {
        Invalid(errors)
      }
  })
  
  implicit val mapping = CaseClassMapping.mapping[Location].verifying(constraint)
}

sealed trait Address {
  val location: Location
}

case class UrbanAddress(
  override val location: Location,
  country: String,
  state: String,
  city: String,
  street: String,
  houseNumber: Int,
  flatNumber: Option[Int]) extends Address {
}

object UrbanAddress {
  implicit val mapping = CaseClassMapping.mapping[UrbanAddress]
}

case class CountrysideAddress(
  override val location: Location,
  country: String,
  state: String,
  county: String,
  houseNumber: String) extends Address
  
object CountrysideAddress {
  implicit val mapping = CaseClassMapping.mapping[CountrysideAddress]
}