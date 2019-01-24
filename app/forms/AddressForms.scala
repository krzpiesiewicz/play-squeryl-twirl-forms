package forms

import scala.language.implicitConversions

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation._
import play.api.mvc.Request

import models._
import models.UrbanAddress

//latitude: Double, longitude: Double
//  override val location: Location,
//  country: String,
//  state: String,
//  city: String,
//  street: String,
//  houseNumber: Int,
//  flatNumber: Option[Int]

object AddressForms {
//  val locationMapping = CaseClassMapping.mapping[Location]

  //  val urbanAddressMapping = CaseClassMapping.mapping[UrbanAddress]
  //  val locationMapping = mapping(
  //      "latitude" -> bigDecimal,
  //      "longitude" -> bigDecimal
  //      )(Location.apply)(Location.unapply)
  //  val urbanAddressForm = Form(
  //  mapping(
  //    "name" -> text,
  //    "age" -> number
  //  )(UrbanAddress.apply)(UrbanAddress.unapply)
  //)

  val locationForm = Form(Location.mapping)
  
  trait AddressForm extends CovariantForm[Address] {
    def create(form: Form[_]): AddressForm
  }
  
  object AddressForm {
//    def bindFromRequest()(implicit request: Request[_]): AddressForm = {
//      request.
//    }
//    implicit def apply[A <: Address](form: Form[A]) = A match
      //new CovariantForm[A](form) with AddressForm
  }
  
  class UrbanAddressForm(form: Form[UrbanAddress]) extends CovariantForm[UrbanAddress](form) with AddressForm {
    override def create(form: Form[_]): UrbanAddressForm = new UrbanAddressForm(form.asInstanceOf[Form[UrbanAddress]])
  }
  
  class CountrysideAddressForm(form: Form[CountrysideAddress]) extends CovariantForm[CountrysideAddress](form) with AddressForm {
    override def create(form: Form[_]): CountrysideAddressForm = new CountrysideAddressForm(form.asInstanceOf[Form[CountrysideAddress]])
  }
  
  val urbanAddressForm = new UrbanAddressForm(Form(UrbanAddress.mapping))
  
  val countrysideAddressForm = new CountrysideAddressForm(Form(CountrysideAddress.mapping))
  
//  val urbanAddressForm = new CovariantForm(Form(UrbanAddress.mapping))
//  
//  val countrysideAddressForm = new CovariantForm(Form(CountrysideAddress.mapping))
}