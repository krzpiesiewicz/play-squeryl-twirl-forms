package database

import org.squeryl._
import org.squeryl.dsl._
import org.squeryl.annotations.Column
import java.util.Date
import java.sql.Timestamp

import CustomTypeMode._

import scala.language.implicitConversions
import scala.language.higherKinds
import scala.collection.generic.CanBuildFrom

object DBSchema extends Schema {

  import models._

  class DbObject extends KeyedEntity[Long] {
    val id: Long = 0
  }

  case class LocationDAO(
    val latitude: Double,
    val longitude: Double) extends DbObject

  object LocationDAO {
    implicit def apply(loc: Location): LocationDAO = new LocationDAO(loc.latitude, loc.longitude)
  }

  class AddressDAO(
    val locationId: Long,
    val typeId: Int) extends DbObject {
    lazy val location: LocationDAO = locations.lookup(locationId).get
  }

  object AddressDAO {
    //    implicit def apply(adr: Address): AddressDAO = adr match {
    //      case ua: UrbanAddress => new UrbanAddress(location)
    //    }
  }

  class UrbanAddressDAO(
    locationId: Long,
    val country: String,
    val state: String,
    val city: String,
    val street: String,
    val houseNumber: Int,
    val flatNumber: Option[Int]) extends AddressDAO(locationId, 1) {

    def this() = this(0, "", "", "", "", 0, Some(0))
  }

  object implicits {
    implicit def toLocation(dao: LocationDAO): Location = new Location(dao.latitude, dao.longitude)

    import scalaz.Functor
    import scala.language.implicitConversions

    //    implicit def getFunctor[F[_] <: Iterable[_]] = new Object {
    //      def map1[A, B](fa: F[A])(f: (A) ⇒ B): F[B] = fa.map(f)
    //    }

    //    implicit def map[F[_]: Functor, A, B](fa: F[A])(implicit f: A => B): F[B] = implicitly[Functor[F]].map(fa)(f)

    //    implicit def map[A, B, F[_] <: Iterable[_]](collection: F[A])(implicit f: A => B): F[B] = collection.map {f(_)}

    //    implicit def map[A, B](collection: Iterable[A])(implicit f: A => B): Iterable[B] = collection.map {f(_)}

    implicit class IterableOf[A, That](it: Iterable[A]) {
      implicit def of[B](implicit f: A => B, bf: CanBuildFrom[Iterable[A], B, That]): That = it.map(f)(bf)
    }
    
//    implicit class IterableOf[A](val it: Iterable[A]) {
//      def of[B](implicit f: A => B): Iterable[B] = it map f
//    }

    implicit def toAddress(dao: AddressDAO): Address = dao.typeId match {
      case 1 => urbanAddresses.lookup(dao.id).get
      case 2 => contrysideAddresses.lookup(dao.id).get
      case _ => throw new Exception("Unknkown address type")
    }

    implicit def toUrbanAddress(dao: UrbanAddressDAO): UrbanAddress = new UrbanAddress(
      dao.location,
      dao.country,
      dao.state,
      dao.city,
      dao.street,
      dao.houseNumber,
      dao.flatNumber)

    implicit def toCountrysideAddress(dao: CountrysideAddressDAO): CountrysideAddress = new CountrysideAddress(
      dao.location,
      dao.country,
      dao.state,
      dao.county,
      dao.houseNumber)
  }

  class CountrysideAddressDAO(
    locationId: Long,
    val country: String,
    val state: String,
    val county: String,
    val houseNumber: String) extends AddressDAO(locationId, 2) {

  }

  import implicits._

  val locations = table[LocationDAO]("locations")

  val addresses = table[AddressDAO]("addresses")

  //  val addressToLocation = oneToManyRelation(locations, addresses).via((l, a) => a.locationID === l.id)

  val urbanAddresses = table[UrbanAddressDAO]("urban_addresses")

  val contrysideAddresses = table[CountrysideAddressDAO]("countryside_addresses")
  
  
  
  class Airport(val id: Int, val name: String, val address: Address) extends KeyedEntity[Int]

  val planes = table[Plane]("planes")

  on(planes) { p =>
    declare {
      p.code is (unique)
    }
  }

  val airports = table[Airport]("airports")

  on(airports) { a =>
    declare {
      a.id is (autoIncremented)
    }
  }
}