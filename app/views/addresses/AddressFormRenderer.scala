package views.addresses

import scala.reflect.{ClassTag,classTag}
import play.api.data.Form
import play.twirl.api.Html
import play.api.i18n.Messages
import play.api.mvc.RequestHeader

import models._
import forms.Forms._
import forms.TypedForm
import forms.TraitForm
import forms.TraitFormRenderer

class AddressFormRenderer()(implicit messages: Messages, request: RequestHeader) extends TraitFormRenderer[Address] {

  override def renderFields(form: TraitForm[Address], prefixKey: String): Html = form match {

    case form: TypedForm[Address @unchecked, UrbanAddress @unchecked] if form.tag == classTag[UrbanAddress] =>
      Html(views.html.addresses.urbanAddressFields(form.form, prefixKey).toString)

    case form: TypedForm[Address @unchecked, CountrysideAddress @unchecked] if form.tag == classTag[CountrysideAddress] =>
      Html(views.html.addresses.countrysideAddressFields(form.form, prefixKey).toString)

    case _ => Html(s"Not known type ${form.getClass()} extending TraitForm[Address]")
  }
}