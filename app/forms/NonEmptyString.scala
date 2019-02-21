package forms

import scala.language.implicitConversions

import play.api.data.Forms._
import play.api.data.{Mapping, FieldMapping}
import play.api.data.validation.Constraints._
import play.api.data.format.Formatter
import play.api.data.validation.ValidationError
import play.api.data.validation.Constraint
import play.api.data.validation.Invalid
import play.api.data.validation.Valid

case class NonEmptyString(val s: String) {
  override def toString = s
}

object NonEmptyString {
  
  implicit def apply(s: String) = new NonEmptyString(s)
  
  implicit def toScalaString(nes: NonEmptyString) = nes.s
  
  implicit object formatter extends Formatter[NonEmptyString] {
    
    override val format = nonEmptyText.format
    
    override def bind(key: String, data: Map[String, String]) = single(key -> nonEmptyText).bind(data) match {
      case Left(seq) => Left(seq)
      case Right(s) => Right(NonEmptyString(s))
    }
    
    override def unbind(key: String, value: NonEmptyString) = Map(key -> value.toString)
  }
  
  val constraint = Constraints.createConstraint[NonEmptyString]("constraint.required", Seq(nes => {
    if (nes.s.isEmpty())
      Invalid(Seq(ValidationError(Seq("error.required"), Seq.empty)))
    else
      Valid
      }))
  
  implicit val mapping: Mapping[NonEmptyString] = of[NonEmptyString](formatter).verifying(constraint)
}