package views.traitformswitcher

import scala.reflect.{ ClassTag, classTag }
import play.twirl.api.Html
import play.api.i18n.Messages
import play.api.mvc.RequestHeader

import forms.ClassTagsHelper._
import forms.TraitForm
import forms.TraitFormRenderer

object TraitFormSwitcherHelper {

  def radioClass(switcherId: String, prefixKey: String) = saveString(prefixKey + "_" + switcherId + "_Radio")

  def divName(switcherId: String, prefixKey: String) = saveString(prefixKey + "_" + switcherId + "_FieldsDiv")

  def divId[T](switcherId: String, prefixKey: String, tag: ClassTag[T]) = saveString(prefixKey + "_" + switcherId + "_Div_" + tag.toString())

  def keyAndVal[T](traitForm: TraitForm[T]): (ClassTag[_ <: T], TraitForm[T]) = (traitForm.tag, traitForm)

  def keysAndVals[T](traitForms: Seq[TraitForm[T]]): Seq[(ClassTag[_ <: T], TraitForm[T])] = traitForms map keyAndVal

  def renderRadios[T](tagsAndLabelsMsgs: Seq[(ClassTag[_ <: T], String)], switcherId: String, prefixKey: String = "", selected: ClassTag[_] = classTag[Nothing])
  (implicit messages: Messages): Html = Html(
    tagsAndLabelsMsgs.foldLeft[String]("") { (acc, elem) =>
      {
        val (tag, labelMsgName) = elem
        acc + radioDiv(tag, switcherId, prefixKey, messages(labelMsgName), selected).toString
      }
    })

  def renderDivsWithFields[T](defaultForms: Map[ClassTag[_ <: T], TraitForm[T]], switcherId: String, prefixKey: String = "", filledForms: Seq[TraitForm[T]] = Seq.empty)
  (implicit messages: Messages, request: RequestHeader, renderer: TraitFormRenderer[T]): Html = Html {
    (defaultForms ++ keysAndVals(filledForms)).values.foldLeft("") {
      (acc, traitForm) => acc + divWithFields(switcherId, prefixKey, traitForm).toString
    }
  }

  private def divWithFields[T](switcherId: String, prefixKey: String, traitForm: TraitForm[T])(implicit messages: Messages, renderer: TraitFormRenderer[T]) = Html {
    s"""
    |<div name="${divName(switcherId, prefixKey)}" id="${divId(switcherId, prefixKey, traitForm.tag)}" hidden>
    |	${traitForm.renderFields()}
  	|</div>""".stripMargin
  }

  private def radioDiv[T](tag: ClassTag[T], switcherId: String, prefixKey: String, label: String, selected: ClassTag[_]) = Html {
    s"""
    |<div>
    |	<input class="${radioClass(switcherId, prefixKey)}" type="radio" value="${divId(switcherId, prefixKey, tag)}" ${if (tag == selected) "checked" else ""}>
   	| 	<label> $label</label>
  	|</div>""".stripMargin
  }
}