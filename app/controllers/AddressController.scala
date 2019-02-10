package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.{Lang, Langs}
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi
import play.api.i18n.Messages
import play.api.http._
import play.api.http.Status._

import org.squeryl.{ Session, SessionFactory }

import database.CustomTypeMode._
import database.{ DBSchema => schema }
import schema._
import schema.implicits._

import models._
import forms.Forms

@Singleton
class AddressController @Inject() (cc: ControllerComponents) extends AbstractController(cc) with play.api.i18n.I18nSupport {

  def location() = Action { implicit request: Request[AnyContent] =>
    {
      Ok(views.html.location(Forms.locationForm))
    }
  }

  def locationPost = Action { implicit request: Request[AnyContent] =>
    {
      Forms.locationForm.bindFromRequest.fold(
        formWithErrors => {
          BadRequest(views.html.location(formWithErrors))
        },
        location => {
          Ok(views.html.location(Forms.locationForm.fill(location)))
        })
    }
  }

  def addresses() = Action { implicit request: Request[AnyContent] =>
    addressesHelp()
  }

  def addressesWithOneSelected(addressId: Long) = Action { implicit request: Request[AnyContent] =>
    addressesHelp(Some(addressId))
  }
  
  def addressDelete(addressId: Long) = Action { implicit request: Request[AnyContent] =>
    transaction {
      schema.addresses.delete(addressId)
    } match {
      case true => Redirect(routes.AddressController.addresses())
      case false => addressesHelp(
          Some(addressId),
          None,
          Some(Messages("address.delete.error")),
          Some(routes.AddressController.addressesWithOneSelected(addressId).url),
          Some(BAD_REQUEST))
    }
  }

  def urbanAddressPost = addressPostHelp(Forms.urbanAddressForm)

  def countrysideAddressPost = addressPostHelp(Forms.countrysideAddressForm)

  def addressPostHelp(addressForm: Forms.AddressForm) = Action { implicit request: Request[AnyContent] =>
    {
      val idOpt = Form(single("id" -> longNumber)).bindFromRequest().value

      addressForm.form.bindFromRequest.fold(
        formWithErrors => {
          addressesHelp(idOpt, Some(addressForm.create(formWithErrors)), None, None, Some(BAD_REQUEST))
        },
        address => {
          val (id, msgOpt, statusCode) = transaction {
            idOpt match {
              case None => {
                val dao = schema.addresses.insert(address)
                (dao.id, Some(Messages("addresses.addressCreated")), CREATED)
              }
              case Some(id) => {
                schema.addresses.update(id, address)
                (id, None, ACCEPTED)
              }
            }
          }
          addressesHelp(
              Some(id),
              Some(addressForm.create(addressForm.form.fillAndValidate(address))),
              msgOpt,
              Some(routes.AddressController.addressesWithOneSelected(id).url),
              Some(statusCode))
        })
    }
  }

  import AddressController._

  def addressesHelp(addressIdOpt: Option[Long] = None, addressFormOpt: Option[Forms.AddressForm] = None, msgOpt: Option[String] = None,
      urlToRedirectOpt: Option[String] = None, statusOpt: Option[Int] = None)
  (implicit request: Request[AnyContent]) = transaction {
    val listOfAddresses = schema.addresses.table.allRows
    val (addressProps, statusCode) = addressIdOpt match {
      case Some(addressId) => {
        schema.addresses.lookup(addressId) match {
          case Some(dao) => {
            val addressForm = addressFormOpt match {
              case Some(addressForm) => addressForm
              case None => {
                dao.asInstanceOf[Address] match {
                  case ua: UrbanAddress => Forms.urbanAddressForm.create(Forms.urbanAddressForm.form.fill(ua))
                  case ca: CountrysideAddress => Forms.countrysideAddressForm.create(Forms.countrysideAddressForm.form.fill(ca))
                }
              }
            }
            (EditAddress(addressId, addressForm), statusOpt.getOrElse(OK))
          }
          case None => (AddressNotFound(addressId), NOT_FOUND)
        }
      }
      case None => {
        val props = addressFormOpt match {
          case Some(addressForm) => NewAddress(addressForm)
          case None => NewAddress(Forms.urbanAddressForm)
        }
        (props, statusOpt.getOrElse(OK))
      }
    }
    Status(statusCode)(views.html.addresses(listOfAddresses, addressProps, msgOpt, urlToRedirectOpt))
  }
}

object AddressController {
  sealed trait AddressProps

  case class AddressNotFound(addressId: Long) extends AddressProps
  case class EditAddress(addressId: Long, addressForm: Forms.AddressForm) extends AddressProps
  case class NewAddress(addressForm: Forms.AddressForm) extends AddressProps
}
