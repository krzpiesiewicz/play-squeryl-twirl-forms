package controllers

import com.google.inject._

import play.api.mvc._
import play.api.i18n.I18nSupport

import models.Location
import play.api.data.Form

@Singleton
class LocationController @Inject() (cc: ControllerComponents) extends AbstractController(cc) with I18nSupport {
  
  val locationForm = Form(Location.mapping)
  
  def location() = Action { implicit request: Request[AnyContent] =>
    {
      Ok(views.html.simpleLocationPage(locationForm))
    }
  }

  def locationPost = Action { implicit request: Request[AnyContent] =>
    {
      locationForm.bindFromRequest.fold(
        formWithErrors => {
          BadRequest(views.html.simpleLocationPage(formWithErrors))
        },
        location => {
          Ok(views.html.simpleLocationPage(locationForm.fill(location)))
        })
    }
  }
}