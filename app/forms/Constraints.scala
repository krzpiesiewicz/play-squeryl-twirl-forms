package forms

import play.api.data.validation._

object Constraints {
  
  def createConstraint[T](name: String, validators: Seq[(T) => ValidationResult]): Constraint[T] = Constraint(name)({
    t =>
      validators.foldLeft[ValidationResult](Valid)((res, validator) => validator(t) match {
        case Valid => res
        case inv: Invalid => res match {
          case Valid => inv
          case invRes: Invalid => invRes ++ inv
        }
      })
  })
}