package database

import org.squeryl._
import org.squeryl.dsl._
import scala.reflect.ManifestFactory.classType

import CustomTypeMode._

object DBSchema1 extends Schema {
  
  import Addresses1._
  
  class Plane(val id: String, val seats: Int) extends KeyedEntity[String] {
    def code = id
  }
  
  class Airport(val id: Int, val name: String, val address: Address) extends KeyedEntity[Int]

  val planes = table[Plane]("planes")
  
  val locations = table[Location]("locations")
  
  on(locations) { l =>
    declare {
      l.id is (autoIncremented)
    }
  }
  
  val addresses = table[Address]("addresses")
  
  on(addresses) { a =>
    declare {
      a.id is (autoIncremented)
    }
  }
  
  val addressToLocation = oneToManyRelation(locations, addresses).via((l, a) => a.locationID === l.id)
  
  val urbanAddresses = table[UrbanAddress]("urban_addresses")

  val airports = table[Airport]("airports")

  on(airports) { a =>
    declare {
      a.id is (autoIncremented)
    }
  }
}