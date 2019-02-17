package forms

import scala.reflect.runtime.universe._

import play.api.data._
import play.api.data.Forms._
import play.twirl.api.Html

import models._
import play.api.i18n.Messages
import play.api.mvc.RequestHeader
import scala.reflect._
import play.api.mvc.Request

import ClassTagsHelper._
import play.api.mvc.AnyContent

object Forms {
  
  val locationForm = Form(Location.mapping)
  
  object addressFormHelper extends TraitFormHelper[Address] {
    
    override def getTraitFormFromRequest(prefixKey: String = "")(implicit request: Request[AnyContent]) = {
      
      val cannot = "Cannot find proper TypedForm[Address, _ <: Address]."
      
      Form(single(classTagFieldName(prefixKey) -> text)).bindFromRequest().fold(
          formWithErrors => Left(s"$cannot Form does not contain field: ${classTagFieldName(prefixKey)}"),
          classTagString => classTagString match {
            case s if s == classTag[UrbanAddress].toString => Right(urbanAddressForm)
            case s if s == classTag[CountrysideAddress].toString => Right(countrysideAddressForm)
            case _ => Left(s"$cannot Unknown classTag: $classTagString")
          }
      )
    }
    
    override def getTraitFormForModel(address: Address) = address match {
      case uaf: UrbanAddress => urbanAddressForm
      case caf: CountrysideAddress => countrysideAddressForm
    }
  }
  
  implicit val urbanAddressForm = new TypedForm[Address, UrbanAddress](Form(UrbanAddress.mapping))
  
  implicit val countrysideAddressForm = new TypedForm[Address, CountrysideAddress](Form(CountrysideAddress.mapping))
}