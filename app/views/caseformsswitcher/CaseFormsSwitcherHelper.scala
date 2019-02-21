package views.caseformsswitcher

import scala.reflect.{ ClassTag, classTag }
import play.twirl.api.Html
import play.api.i18n.Messages
import play.api.mvc.RequestHeader
import play.api.data.Form

import forms.ClassTagsHelper._

object CaseFormsSwitcherHelper {

  def divName(form: Form[_], prefixKey: String) = saveString(s"""${prefixKey}_${form.hashCode}_FieldsDiv""")

  def divIdPrefix(form: Form[_], prefixKey: String) = saveString(s"""${prefixKey}_${form.hashCode}_Div_""")
  
  def divId[T](form: Form[_], prefixKey: String, tag: ClassTag[T]) = divIdPrefix(form: Form[_], prefixKey: String) + saveString(tag.toString)
  
  def switcherId(form: Form[_], prefixKey: String) = saveString(s"""${prefixKey}_${form.hashCode}_SwitcherDiv""")
}