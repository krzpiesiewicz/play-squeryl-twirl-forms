package forms

import play.api.data.Form
import scala.reflect.runtime.universe._
import scala.language.implicitConversions

class CovariantForm[+T](f: Form[T]) {
  def form[G >: T] = f.asInstanceOf[Form[G]] //Form[T](f.mapping, f.data, f.errors, f.value)
}

object CovariantForm {
  implicit def toForm[T](cf: CovariantForm[T]): Form[T] = cf.form 
}

//class CovariantForm[+T](val form: Form[T])(implicit val typetag: TypeTag[T]) extends Form[T](form.mapping, form.data, form.errors, form.value) {
//  type Type = T
//}

//object CovariantForm {
//  def apply[T](form: Form[T])(implicit typetag: TypeTag[T]) = new CovariantForm(form)
//  def unapply[T](eform: CovariantForm[T]) = eform 
//}