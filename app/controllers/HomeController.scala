package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.{ Langs, Lang }

import org.squeryl.{ Session, SessionFactory }

import database.CustomTypeMode._
import database.{ DBSchema => schema }
import schema._

import database.DBSchema.LocationDAO
import database.DBSchema.implicits._
import models.Location

@Singleton
class HomeController @Inject() (cc: ControllerComponents) extends AbstractController(cc) with play.api.i18n.I18nSupport {

  def index() = Action { implicit request: Request[AnyContent] =>
    {
      Ok(views.html.index())
    }
  }

  def langRedirection() = Action { implicit request: Request[AnyContent] =>
    {
      val defaultUrl = routes.HomeController.index().url
      
      val (url, langOpt) = Form(
        tuple(
          "langCode" -> text,
          "uri" -> text,
          "method" -> text)).bindFromRequest().value match {
          case Some((langCode, uri, method)) => {
            val lang = Lang(langCode)
            
            val langOpt = if (main.ScalaI18nService.availableLangs.contains(lang))
              Some(lang)
            else
              None
              
            val url = method match {
              case "GET" => uri
              case _ => defaultUrl
            }
            
            (url, langOpt)
          }
          case None => (defaultUrl, None)
        }
      
      langOpt match {
        case Some(lang) => Redirect(url).withLang(lang)
        case None => Redirect(url)
      }
    }
  }
}