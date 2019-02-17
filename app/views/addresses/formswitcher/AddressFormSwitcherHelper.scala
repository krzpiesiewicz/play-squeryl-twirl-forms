package views.addresses.formswitcher

import scala.reflect.{classTag, ClassTag}

import models._
import forms._
import forms.Forms._
import forms.ClassTagsHelper._
import views.traitformswitcher.TraitFormSwitcherHelper._

object AddressFormSwitcherHelper {

  val defaultForms: Map[ClassTag[_ <: Address], TraitForm[Address]] = keysAndVals(Seq(
    urbanAddressForm,
    countrysideAddressForm)).toMap

  val tagAndLabelsMsgs = Seq(
    (classTag[UrbanAddress], "addresses.urbanAddress"),
    (classTag[CountrysideAddress], "addresses.countrysideAddress"))
}