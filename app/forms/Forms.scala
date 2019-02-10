package forms

import scala.reflect.runtime.universe._

import play.api.data._
import play.api.data.Forms._

import models._

object Forms {
  
  val locationForm = Form(Location.mapping)
  
  sealed trait AddressForm {
    type T
    type A = T with Address
    val form: Form[A]
    def create(form: Form[A]): AddressForm
  }
  
  class UrbanAddressForm(override val form: Form[UrbanAddress]) extends AddressForm {
    type T = UrbanAddress
    override def create(form: Form[A]): UrbanAddressForm = new UrbanAddressForm(form.asInstanceOf[Form[UrbanAddress]])
  }
  
  class CountrysideAddressForm(override val form: Form[CountrysideAddress]) extends AddressForm {
    type T = CountrysideAddress
    override def create(form: Form[A]): CountrysideAddressForm = new CountrysideAddressForm(form.asInstanceOf[Form[CountrysideAddress]])
  }
  
  val urbanAddressForm = new UrbanAddressForm(Form(UrbanAddress.mapping))
  
  val countrysideAddressForm = new CountrysideAddressForm(Form(CountrysideAddress.mapping))
}