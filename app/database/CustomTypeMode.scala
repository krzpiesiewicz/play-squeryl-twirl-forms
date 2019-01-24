package database

import org.squeryl.SessionFactory
import org.squeryl.customtypes.CustomType

object CustomTypeMode extends org.squeryl.PrimitiveTypeMode {
  trait Domain[A] {
    self: CustomType[A] =>

    def label: String
    def validate(a: A): Unit
    def value: A

    validate(value)
  }
  
  implicit val sessionFactory = SessionFactory.concreteFactory
}