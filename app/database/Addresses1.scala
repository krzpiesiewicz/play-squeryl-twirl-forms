package database

import org.squeryl.customtypes.CustomTypesMode._
import org.squeryl.customtypes._
import org.squeryl.KeyedEntity
import CustomTypeMode._
import DBSchema1._
import org.squeryl.dsl.ManyToOne
import org.squeryl.dsl.OneToMany

object Addresses1 {

  class Location(val id: Long, val latitude: Double, val longitude: Double) extends KeyedEntity[Long] {
    assert(latitude >= -90 && latitude <= 90, "latitude val should be in interval [-90, 90].")
    assert(longitude >= -180 || longitude < 180, "longitude val should be in interval [-180, 180).")

    lazy val address: OneToMany[Address] = addressToLocation.left(this)
  }

  class Address(
    val id: Long,
    val locationID: Long) extends KeyedEntity[Long] {

    lazy val location: ManyToOne[Location] = addressToLocation.right(this)
  }

  class UrbanAddress(
    id: Long,
    locationID: Long,
    val country: String,
    val city: String,
    val street: String,
    val houseNumber: Int,
    val flatNumber: Option[Int]) extends Address(id, locationID) {
  }

  class ContrysideAddress(
    id: Long,
    locationID: Long,
    country: String,
    county: String,
    houseNumber: Int) extends Address(id, locationID)

  class Age(v: Int) extends IntField(v) with Domain[Int] {
    def validate(a: Int) = assert(a > 0, "age must be positive, got " + a)
    def label = "age"
  }

  class FirstName(v: String) extends StringField(v) with Domain[String] {
    def validate(s: String) = assert(s.length <= 50, "first name is waaaay to long : " + s)
    def label = "first name"
  }

  class WeightInKilograms(v: Double) extends DoubleField(v) with Domain[Double] {
    def validate(d: Double) = assert(d > 0, "weight must be positive, got " + d)
    def label = "weight (in kilograms)"
  }

  class Patient(val firstName: FirstName, val age: Age, val weight: WeightInKilograms)
}