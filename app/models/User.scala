package models

import play.api.data._
import play.api.data.validation._
import play.api.data.Forms._
import play.api.data.FormError
import play.api.data.Mapping
import play.api.data.format.Formatter

import java.util.Date
import org.mindrot.jbcrypt.BCrypt

object Sex extends Enumeration {
  type Sex = Value
  val MALE = Value("male")
  val FEMALE = Value("female")
  
  def apply(s: String): Sex = withName(s)
  
  implicit object formatter extends Formatter[Sex] {
    
    override def bind(key: String, data: Map[String, String]) = data.get(key).map(withName(_)).toRight(Seq(FormError(key, "error.required", Nil)))
    
    override def unbind(key: String, value: Sex) = Map(key -> value.toString)
  }
}

import Sex.Sex

case class UserData(
    username: String,
    password: String,
    firstName: String,
    lastName: String,
    sex: Sex,
    dateOfBirth: Date,
    address: Address,
    phoneNumber: String,
    emails: List[String],
    aboutMe: String,
    confirmation: Boolean,
    subscription: Boolean)

object UserData {
  implicit val mapping = play.api.data.Forms.mapping(
    "username" -> nonEmptyText(3, 50),
    "password" -> nonEmptyText(minLength = 6),
    "firstName" -> nonEmptyText,
    "lastName" -> nonEmptyText,
    "sex" -> of[Sex],
    "dateOfBirth" -> date("yyyy-MM-dd"),
    "address" -> Address.mapping,
    "phoneNumber" -> text.verifying(constraint = _.matches("""[+][\d]{1,4}([\s-]*[\d]{3}){3}"""), error="error.phoneNumber"),
    "emails" -> list(email),
    "aboutMe" -> text,
    "confirmation" -> checked("constraint.confirmation"),
    "subscription" -> boolean)(UserData.apply)(UserData.unapply)
}
