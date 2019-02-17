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
import play.api.mvc.AnyContent

trait TraitFormRenderer[A] {   
  //def renderFields[T1 <: A](form: TypedForm[A, T1], prefixKey: String): Html
  def renderFields(form: TraitForm[A], prefixKey: String): Html
}

trait TraitForm[A] {
  protected type T
  type AT = T with A
  
  def tag: ClassTag[AT]
  
  def form: Form[AT]
  
  def create(form: Form[AT]): TraitForm[A]
  
  def renderFields(prefixKey: String = "")(implicit renderer: TraitFormRenderer[A]): Html
}

trait TraitFormHelper[A] {
  def getTraitFormFromRequest(prefixKey: String = "")(implicit request: Request[AnyContent]): Either[String, TraitForm[A]] 
  
  def getTraitFormForModel(a: A): TraitForm[A]
  
  def getTraitFormForModelAndFill(a: A): TraitForm[A] = {
    val traitForm = getTraitFormForModel(a)
    traitForm.create(traitForm.form.fill(a.asInstanceOf[traitForm.AT]))
  }
  
  def getTraitFormForModelAndFillAndValidate(a: A): TraitForm[A] = {
    val traitForm = getTraitFormForModel(a)
    traitForm.create(traitForm.form.fillAndValidate(a.asInstanceOf[traitForm.AT]))
  }
}

class TypedForm[A, T1 <: A](f: Form[T1])(implicit ctag: ClassTag[T1]) extends TraitForm[A] {
  type T = T1
  
  override val tag: ClassTag[AT] = ctag
  
  override def form = f
  
  override def create(form: Form[AT]): TypedForm[A, AT] = new TypedForm[A, AT](form)
  
  override def renderFields(prefixKey: String = "")(implicit renderer: TraitFormRenderer[A]): Html = renderer.renderFields(this, prefixKey)
}