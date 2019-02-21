package controllers

import com.google.inject._

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._

@Singleton
class UserController @Inject() (cc: ControllerComponents) extends AbstractController(cc) with play.api.i18n.I18nSupport {

  val userDataForm = Form(UserData.mapping)

  def users() = Action { implicit request: Request[AnyContent] =>
    {
      Ok(views.html.usersPage(userDataForm))
    }
  }

  def userPost() = Action { implicit request: Request[AnyContent] =>
    {     
      val (dataForm, userDataOpt) = userDataForm.bindFromRequest.fold(
        formWithErrors => (formWithErrors, None),
        userData => (userDataForm.fill(userData), Some(userData))
      )
      Ok(views.html.usersPage(dataForm, userDataOpt))
    }
  }
}