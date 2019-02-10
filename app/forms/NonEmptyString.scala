package forms

import play.api.data.Forms._
import play.api.data.Mapping
import play.api.data.format.Formatter

class NonEmptyString(val s: String) {
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
  
  implicit val mapping: Mapping[NonEmptyString] = of[NonEmptyString]
}