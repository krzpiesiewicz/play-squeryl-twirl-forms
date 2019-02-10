package forms

import play.api.data.FormError
import play.api.data.Mapping

import forms.FormValidators.FormValidator

class MappingWithValidator[T](mapping: Mapping[T], formValidator: FormValidator[T]) extends Mapping[T] {
  
  def bind(data: Map[String, String]) = mapping.bind(data) match {
    case Left(seq) => Left(seq)
    case Right(t) => {
      val seq = formValidator(prefix, t)
      if (seq.isEmpty)
        Right(t)
      else
        Left(seq)
    }
  }
  
  val constraints = mapping.constraints
  
  val key = mapping.key
  
  val mappings = mapping.mappings
  
  def unbind(value: T) = mapping.unbind(value)
  
  def unbindAndValidate(value: T) = {
    val (map, seq) = mapping.unbindAndValidate(value)
    (map, seq ++ formValidator(prefix, value))
  }
  
  def verifying(constraints: play.api.data.validation.Constraint[T]*) = new MappingWithValidator(
      constraints.foldLeft(mapping)((mapping, constraint) => mapping.verifying(constraint)),
      formValidator)
  
  def withPrefix(prefix: String) = new MappingWithValidator(mapping.withPrefix(prefix), formValidator)
  
  private val prefix = key match {
    case "" => ""
    case s => s + "."
  }
}