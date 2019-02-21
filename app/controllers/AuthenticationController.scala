package controllers

import com.google.inject._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages

@Singleton
class AuthenticationController @Inject() (cc: ControllerComponents) extends AbstractController(cc) with i18n.I18nSupport {

  val loginForm = Form(tuple("username" -> text, "password" -> text))

  def login() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.loginPage(loginForm))
  }
  
  def loginPost() = Action { implicit request: Request[AnyContent] =>
    {
      loginForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.loginPage(formWithErrors)),
        tuple => {
          val (username, password) = tuple
          if (Seq(("admin", "123"), ("tester", "456")) contains (username, password)) 
            Redirect(routes.HomeController.index()).withSession(request.session + ("username" -> username) + ("password" -> password))
          else
            BadRequest(views.html.loginPage(loginForm.fill(tuple).withError(FormError("", Messages("error.login")))))
        })
    }
  }

  def logout() = Action { implicit request: Request[AnyContent] =>
    {
      Redirect(routes.HomeController.index()).withSession(request.session - "username" - "password")
    }
  }
}