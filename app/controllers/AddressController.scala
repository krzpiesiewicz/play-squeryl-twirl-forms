package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi

import org.squeryl.{ Session, SessionFactory }

import database.CustomTypeMode._
import database.{ DBSchema => schema }
import schema._
import schema.implicits._

import models._
import forms.AddressForms._
import forms.AddressForms
import forms.CovariantForm

@Singleton
class AddressController @Inject() (cc: ControllerComponents) extends AbstractController(cc) with play.api.i18n.I18nSupport {

  def location() = Action { implicit request: Request[AnyContent] =>
    {
      Ok(views.html.location(locationForm))
    }
  }

  def locationPost = Action { implicit request: Request[AnyContent] =>
    {
      locationForm.bindFromRequest.fold(
        formWithErrors => {
          BadRequest(views.html.location(formWithErrors))
        },
        location => {
          Ok(views.html.location(locationForm.fill(location)))
        })
    }
  }

  def addresses() = Action { implicit request: Request[AnyContent] =>
    addressesHelp(NoParam)
  }

  def addressesWithOneSelected(addressId: Long) = Action { implicit request: Request[AnyContent] =>
    addressesHelp(AddressIdParam(addressId))
  }

  def urbanAddressPost = addressPostHelp(urbanAddressForm)

  def countrysideAddressPost = addressPostHelp(countrysideAddressForm)


  def addressPostHelp(addressForm: AddressForm) = Action { implicit request: Request[AnyContent] =>
    {
      val form = addressForm.form
      println(addressForm)
      println(form)
      form.bindFromRequest.fold(
        formWithErrors => {
          addressesHelp(AddressFormParam(addressForm.create(formWithErrors)))
        },
        address => {
          addressesHelp(AddressFormParam(addressForm.create(form.fillAndValidate(address))))
        })
    }
  }
  
  sealed trait AddressesHelpParam

  case class AddressIdParam(addressId: Long) extends AddressesHelpParam
  case class AddressFormParam(form: AddressForm) extends AddressesHelpParam
  case object NoParam extends AddressesHelpParam

  def addressesHelp(param: AddressesHelpParam)(implicit request: Request[AnyContent]) = transaction {
    val listOfAddresses = schema.addresses.allRows.of[Address]
    val addressForm: AddressForm = param match {
      case p: AddressIdParam =>
        schema.addresses.lookup(p.addressId) match {
          case Some(dao) => dao.asInstanceOf[Address] match {
            case ua: UrbanAddress => AddressForms.urbanAddressForm
            case ca: CountrysideAddress => AddressForms.countrysideAddressForm
          }
          case None => AddressForms.urbanAddressForm
        }
      case p: AddressFormParam => p.form
      case NoParam => AddressForms.urbanAddressForm
    }
    Ok(views.html.addresses(listOfAddresses, addressForm))
  }

  //  def planes() = Action { implicit request: Request[AnyContent] =>
  //    {
  ////      val list = transaction {
  ////        from(people) {p => where(p.name <> "SIK") select(p)}.toList
  ////      }
  //      transaction {
  //        locations.insert(new Location(59, 40))
  ////        new UrbanAddressDAO(0, "Poland", "mazowieckie", "Warsaw", "Banacha", 2, Some("")).save()
  ////        addresses.insert(new UrbanAddress(new Location(49, 52), "Poland", "mazowieckie", "Warsaw", "Banacha", 2, None))
  //      }
  //      val ads = transaction {
  //        addresses.allRows
  //      }
  //      Ok("Hello " + ads.toList)
  //    }
  //  }
}

object AddressController {
  def addressPostUrl(addressForm: AddressForm) = addressForm match {
    case uaf: UrbanAddressForm => routes.AddressController.urbanAddressPost()
    case caf: CountrysideAddressForm => routes.AddressController.countrysideAddressPost()
  }
}
