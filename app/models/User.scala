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

case class User(
    username: String,
    passwordHash: String,
    firstName: String,
    lastName: String,
    sex: Sex,
    dateOfBirth: Date,
    address: Address,
    phoneNumber: String,
    emails: List[String],
    aboutMe: String)

case class UserData(
    username: String,
    password: String,
    firstName: String,
    lastName: String,
    sex: Sex,
    dateOfBirth: Date,
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
    "dateOfBirth" -> date("dd-MM-yyyy"),
    "phoneNumber" -> text.verifying(constraint = _.matches("""[+][\\d]{1,4}(\\w)*[\\d]{9}"""), error="A valid phone number is required."),
    "emails" -> list(email),
    "aboutMe" -> text,
    "confirmation" -> checked("Please accept the general terms."),
    "subscription" -> boolean)(UserData.apply)(UserData.unapply)
}

object User {
  def apply(data: UserData, address: Address): User = User(
      data.username,
      BCrypt.hashpw(data.password, BCrypt.gensalt()),
      data.firstName,
      data.lastName,
      data.sex,
      data.dateOfBirth,
      address,
      data.phoneNumber,
      data.emails,
      data.aboutMe)
}