package controllers

import javax.inject._

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
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
import forms._
import forms.Forms._

import views.addresses.AddressFormRenderer

@Singleton
class AddressController @Inject() (cc: ControllerComponents) extends AbstractController(cc) with I18nSupport {

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

  def addressPost() = Action { implicit request: Request[AnyContent] =>
    {
      addressFormHelper.getTraitFormFromRequest() match {
        case Left(msg) => addressesHelp(
          msgOpt = Some(Messages("addresses.typeNotChosen")),
          statusOpt = Some(BAD_REQUEST))
        case Right(addressForm) => {

          val idOpt = Form(single("idOpt" -> optional(longNumber))).bindFromRequest().value.getOrElse(None)

          addressForm.form.bindFromRequest.fold(
            formWithErrors => {
              addressesHelp(idOpt, Some(addressForm.create(formWithErrors)), None, None, Some(BAD_REQUEST))
            },
            address => {
              val (idOpt2, traitForm, msgOpt, noRedirection, statusCode) = transaction {
                schema.addresses.insertOrUpdate(address) match {
                  case Left(updated) => {
                    val traitForm = addressForm.create(addressForm.form.fillAndValidate(address))
                    updated match {
                      case false => (idOpt, traitForm, Some(Messages("addresses.cannotUpdateAddress")), true, BAD_REQUEST)
                      case true => (idOpt, traitForm, None, false, ACCEPTED)
                    }
                  }
                  case Right(createdId) => {
                    val traitForm = addressForm.create(addressForm.form.fillAndValidate(address))
                    (Some(createdId), traitForm, Some(Messages("addresses.addressCreated")), false, CREATED)
                  }
                }
              }

              val redirectionOpt = if (noRedirection)
                None
              else idOpt2 match {
                case None => None
                case Some(id) => Some(routes.AddressController.addressesWithOneSelected(id).url)
              }

              addressesHelp(
                idOpt2,
                Some(traitForm),
                msgOpt,
                redirectionOpt,
                Some(statusCode))
            })
        }
      }
    }
  }

  import AddressController._

  def addressesHelp(
    addressIdOpt: Option[Long] = None,
    addressFormOpt: Option[TraitForm[Address]] = None,
    msgOpt: Option[String] = None,
    urlToRedirectOpt: Option[String] = None,
    statusOpt: Option[Int] = None)(implicit request: Request[AnyContent]) = transaction {

    val listOfAddresses = schema.addresses.table.allRows

    val (addressProps, statusCode) = addressIdOpt match {
      case Some(addressId) => {
        schema.addresses.lookup(addressId) match {
          case Some(dao) => {
            val addressForm = addressFormOpt match {
              case Some(addressForm) => addressForm
              case None => addressFormHelper.getTraitFormForModelAndFill(dao)
            }
            (EditAddress(addressId, addressForm), statusOpt.getOrElse(OK))
          }
          case None => (AddressNotFound(addressId), NOT_FOUND)
        }
      }
      case None => {
        val props = addressFormOpt match {
          case Some(addressForm) => NewAddress(addressForm)
          case None => NoProps
        }
        (props, statusOpt.getOrElse(OK))
      }
    }

    implicit val addressFormRenderer: TraitFormRenderer[Address] = new AddressFormRenderer()

    Status(statusCode)(views.html.addressesPage(listOfAddresses, addressProps, msgOpt, urlToRedirectOpt))
  }
}

object AddressController {

  sealed trait AddressProps

  case class AddressNotFound(addressId: Long) extends AddressProps

  case class EditAddress(addressId: Long, addressForm: TraitForm[Address]) extends AddressProps

  case class NewAddress(addressForm: TraitForm[Address]) extends AddressProps

  case object NoProps extends AddressProps
}
