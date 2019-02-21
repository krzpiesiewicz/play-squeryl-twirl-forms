package forms

import play.api.data.validation._
import play.api.data.FormError

object Constraints {

  def toValidators[T](constraints: Seq[Constraint[T]]): Seq[(T) => ValidationResult] =
    constraints map { constraint => constraint(_) }

  def mergeValidators[T](validators: Seq[(T) => ValidationResult]): (T) => ValidationResult = t =>
    validators.foldLeft[ValidationResult](Valid)((res, validator) => validator(t) match {
      case Valid => res
      case inv: Invalid => res match {
        case Valid => inv
        case invRes: Invalid => invRes ++ inv
      }
    })

  def createConstraint[T](name: String, validators: Seq[(T) => ValidationResult]): Constraint[T] = Constraint(name)(mergeValidators(validators))

  def validationResultToSeqOfFormErrors(key: String, res: ValidationResult): Seq[FormError] = res match {
    case Valid => Seq.empty
    case inv: Invalid => inv.errors.map(error => new FormError(key = key, messages = error.messages, args = error.args))
  }
}