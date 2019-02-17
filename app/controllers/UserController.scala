package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import forms.TraitFormRenderer
import views.addresses.AddressFormRenderer
import models._

@Singleton
class UserController @Inject() (cc: ControllerComponents) extends AbstractController(cc) with play.api.i18n.I18nSupport {

  val userDataForm = Form(UserData.mapping)

  def users() = Action { implicit request: Request[AnyContent] =>
    {
      implicit val addressFormRenderer: TraitFormRenderer[Address] = new AddressFormRenderer()
      Ok(views.html.usersPage(userDataForm))
    }
  }

  def userPost() = Action { implicit request: Request[AnyContent] =>
    {
      implicit val addressFormRenderer: TraitFormRenderer[Address] = new AddressFormRenderer()
      
      val (dataForm, userDataOpt) = userDataForm.bindFromRequest.fold(
        formWithErrors => (formWithErrors, None),
        userData => (userDataForm.fill(userData), Some(userData))
      )
      
//      val (addressForm, addressOpt) = {
//        val formOpt = addressFormHelper.getTraitFormFromRequest() match {
//        case Left(msg) => addressesHelp(
//          msgOpt = Some(Messages("addresses.typeNotChosen")),
//          statusOpt = Some(BAD_REQUEST))
//        case Right(addressForm) => {
//
//          val idOpt = Form(single("idOpt" -> optional(longNumber))).bindFromRequest().value.getOrElse(None)
//
//          addressForm.form.bindFromRequest.fold(
//            formWithErrors => {
//              addressesHelp(idOpt, Some(addressForm.create(formWithErrors)), None, None, Some(BAD_REQUEST))
//            },
//            address => {
//      }
      
      Ok(views.html.usersPage(dataForm))
    }
  }
}