package main

import com.softwaremill.macwire._
import play.inject.guice.GuiceApplicationLoader
import play.api.inject.guice.GuiceableModuleConversions
import play.api.ApplicationLoader.Context
import play.api.routing.Router
import play.api.db.DBComponents
import play.api.db.evolutions.EvolutionsComponents
import play.api.db.HikariCPComponents
import play.api.i18n.I18nComponents
import play.api.BuiltInComponentsFromContext
import play.filters.HttpFiltersComponents

import controllers.HomeController
import controllers.LocationController
import controllers.AddressController
import controllers.UserController
import controllers.AssetsComponents
import controllers.AuthenticationController
import controllers.Authentication.UserActionBuilder

import akka.actor.ActorSystem
import router.Routes

import javax.inject.Singleton

import scala.concurrent.ExecutionContext

import play.api.libs.concurrent.CustomExecutionContext
import play.api.inject.guice.GuiceableModule
import com.google.inject._

import play.api.mvc.BodyParsers
import play.api.mvc.BodyParser
import play.api.mvc.AnyContent




class CustomLoader extends GuiceApplicationLoader {
  def load(context: Context) = new ApplicationComponents(context).application
}

trait MyAssetsComponents extends AssetsComponents

class ApplicationComponents(context: Context) extends BuiltInComponentsFromContext(context)
  with HttpFiltersComponents
  with AssetsComponents
  with DBComponents
  with EvolutionsComponents
  with HikariCPComponents
  with I18nComponents {
  
  implicit lazy val implicitExecutionContext: ExecutionContext = executionContext
  implicit lazy val parser: BodyParsers.Default = wire[BodyParsers.Default]
  implicit lazy val userActionBuilder = wire[UserActionBuilder]
  
  lazy val appActorSystem: ActorSystem = actorSystem
  lazy val homeController = wire[HomeController]
  lazy val authenticationController = wire[AuthenticationController]
  lazy val locationController = wire[LocationController]
  lazy val addressController = wire[AddressController]
  lazy val userController = wire[UserController]
//  lazy val router = wire[Routes]
  lazy val router: Router = new Routes(httpErrorHandler, homeController, authenticationController, locationController, addressController, userController, assets)
  
  applicationEvolutions
}