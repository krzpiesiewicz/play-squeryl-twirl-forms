package controllers

import com.google.inject._

import akka.actor.ActorSystem
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi
import play.api.i18n.Messages
import play.api.http._
import play.api.http.Status._
import play.api.libs.concurrent.CustomExecutionContext

import org.squeryl.{ Session, SessionFactory }

import database.CustomTypeMode._
import database.{ DBSchema => schema }
import schema._
import schema.implicits._

import models._

import forms.TraitMapping

import Authentication.{AuthenticatedUserAction, UserActionBuilder, AdminAction}

@Singleton
class AddressController @Inject() (
    cc: ControllerComponents,
    implicit val executionContext: ExecutionContext,
    implicit val parser: BodyParsers.Default,
    implicit val userActionBuilder: UserActionBuilder) extends AbstractController(cc) with I18nSupport {

  import AddressController._

  val addressForm = Form(Address.mapping)

  def addresses() = Action.async { implicit request: Request[AnyContent] => Future {
    addressesHelp()
  }}

  def addressesWithOneSelected(addressId: Long) = Action.async { implicit request: Request[AnyContent] => Future {
    addressesHelp(Some(addressId))
  }}

  def addressDelete(addressId: Long) = AdminAction { implicit request: Request[AnyContent] => Future {
    transaction {
      schema.addresses.delete(addressId)
    } match {
      case true => Redirect(routes.AddressController.addresses())
      case false => addressesHelp(
        Some(addressId),
        None,
        Some(Messages("addresses.delete.error", addressId)),
        Some(routes.AddressController.addressesWithOneSelected(addressId).url),
        Some(BAD_REQUEST))
    }
  }}

  def addressPost() = AuthenticatedUserAction { implicit request: Request[AnyContent] =>
    {
      val idOpt = Form(single(TraitMapping.tagFieldName -> nonEmptyText)).bindFromRequest.value match {
        case None => None
        case Some(tag) => Form(single(s"${tag}.idOpt" -> optional(longNumber))).bindFromRequest().value.getOrElse(None)
      }

      addressForm.bindFromRequest.fold(
        formWithErrors => Future {
          addressesHelp(idOpt, Some(formWithErrors), None, None, Some(BAD_REQUEST))
        },
        address => Future {
          val callback = transaction {
            schema.addresses.insertOrUpdate(address) match {
              case Left(updated) => {
                val redirectionOpt = idOpt match {
                  case None => None
                  case Some(id) => Some(routes.AddressController.addressesWithOneSelected(id).url)
                }
                val (msgOpt, statusCode) = updated match {
                  case false => (Some(Messages("addresses.cannotUpdateAddress")), BAD_REQUEST)
                  case true => (None, OK)
                }
                () => addressesHelp(idOpt, None, msgOpt, redirectionOpt, Some(statusCode))
              }
              case Right(createdId) => () => addressesHelp(
                None,
                Some(addressForm.fill(address)),
                Some(Messages("addresses.addressCreated")),
                Some(routes.AddressController.addressesWithOneSelected(createdId).url),
                Some(CREATED))
            }
          }
          callback()
        })
    }
  }

  def addressesHelp(
    addressIdOpt: Option[Long] = None,
    addressFormOpt: Option[Form[Address]] = None,
    msgOpt: Option[String] = None,
    urlToRedirectOpt: Option[String] = None,
    statusOpt: Option[Int] = None)(implicit request: Request[AnyContent]) = transaction {

    val listOfAddresses = schema.addresses.table.allRows

    val (addressProps, newAddressForm, statusCode) = addressIdOpt match {
      case Some(addressId) => {
        schema.addresses.lookup(addressId) match {
          case Some(dao) => {
            val form = addressFormOpt match {
              case Some(form) => form
              case None => addressForm.fill(dao)
            }
            (EditAddress(addressId, form), addressForm, statusOpt.getOrElse(OK))
          }
          case None => (AddressNotFound(addressId), addressForm, NOT_FOUND)
        }
      }
      case None => (NoProps, addressFormOpt.getOrElse(addressForm), statusOpt.getOrElse(OK))
    }

    Status(statusCode)(views.html.addressesPage(listOfAddresses, addressProps, newAddressForm, msgOpt, urlToRedirectOpt))
  }
}

object AddressController {

  sealed trait AddressProps

  case class AddressNotFound(addressId: Long) extends AddressProps

  case class EditAddress(addressId: Long, addressForm: Form[Address]) extends AddressProps

  case object NoProps extends AddressProps
}
