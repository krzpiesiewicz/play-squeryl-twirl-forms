package database

import org.squeryl._
import org.squeryl.dsl._

import scala.language.implicitConversions
import scala.collection.generic.CanBuildFrom

import CustomTypeMode._

object DBSchema extends Schema {

  import models._

  class DbObject extends KeyedEntity[Long] {
    val id: Long = 0
  }

  class LocationDAO(
    val latitude: Double,
    val longitude: Double,
    override val id: Long = 0) extends DbObject

  object LocationDAO {
    def apply(loc: Location, id: Long = 0): LocationDAO = new LocationDAO(loc.latitude, loc.longitude, id)
    implicit def toLocation(dao: LocationDAO) = Location(dao.latitude, dao.longitude)
  }

  class AddressDAO(
    val locationId: Long,
    val typeId: Int,
    override val id: Long = 0) extends DbObject {

    lazy val location: LocationDAO = locations.lookup(locationId).get

    lazy val address: Address = typeId match {
      case AddressDAO.urbanTypeId => {
        val uaDao = urbanAddresses.lookup(id).get
        UrbanAddress(
          uaDao.country,
          uaDao.state,
          uaDao.city,
          uaDao.street,
          uaDao.houseNumber,
          uaDao.flatNumber,
          location)
      }
      case AddressDAO.countrysideTypeId => {
        val caDao = countrysideAddresses.lookup(id).get
        CountrysideAddress(
          caDao.country,
          caDao.state,
          caDao.county,
          caDao.houseNumber,
          location)
      }
      case _ => throw new Exception("Unknkown address type")
    }
  }

  object AddressDAO {
    val urbanTypeId = 1
    val countrysideTypeId = 2
  }

  class UrbanAddressDAO(
    override val id: Long,
    val country: String,
    val state: String,
    val city: String,
    val street: String,
    val houseNumber: String,
    val flatNumber: Option[Int]) extends DbObject {

    def this() = this(0, "", "", "", "", "", Some(0))
  }

  object UrbanAddressDAO {
    def apply(id: Long, ua: UrbanAddress): UrbanAddressDAO =
      new UrbanAddressDAO(id, ua.country, ua.state, ua.city, ua.street, ua.houseNumber, ua.flatNumber)
  }

  class CountrysideAddressDAO(
    override val id: Long,
    val country: String,
    val state: String,
    val county: String,
    val houseNumber: String) extends DbObject

  object CountrysideAddressDAO {
    def apply(id: Long, ca: CountrysideAddress): CountrysideAddressDAO =
      new CountrysideAddressDAO(id, ca.country, ca.state, ca.county, ca.houseNumber)
  }

  object implicits {
    implicit def toLocation(dao: LocationDAO): Location = new Location(dao.latitude, dao.longitude)

    import scalaz.Functor

    implicit class IterableOf[A, That](it: Iterable[A]) {
      implicit def of[B](implicit f: A => B, bf: CanBuildFrom[Iterable[A], B, That]): That = it.map(f)(bf)
    }

    implicit def toAddress(dao: AddressDAO): Address = dao.address
  }

  import implicits._

  val locations = table[LocationDAO]("locations")

  private val addressesTable = DBSchema.table[AddressDAO]("addresses")

  val urbanAddresses = table[UrbanAddressDAO]("urban_addresses")

  val countrysideAddresses = table[CountrysideAddressDAO]("countryside_addresses")

  on(urbanAddresses)(ua => declare(
    ua.id is (primaryKey)))

  on(countrysideAddresses)(ca => declare(
    ca.id is (primaryKey)))

  //  val addressToLocation = oneToManyRelation(locations, addresses.table).via((l, a) => a.locationId === l.id)
  //
  //  val urbanAddressToAddress = oneToManyRelation(addressesTable, urbanAddresses).via((a, ua) => a.id === ua.id)
  //
  //  val countrysideAddressToAddress = oneToManyRelation(addressesTable, countrysideAddresses).via((a, ca) => a.id === ca.id)

  object addresses {
    val table = addressesTable

    def delete(id: Long): Boolean = inTransaction {
      table.lookup(id) match {
        case Some(dao) => {
          dao.typeId match {
            case AddressDAO.urbanTypeId => urbanAddresses.delete(id)
            case AddressDAO.countrysideTypeId => countrysideAddresses.delete(id)
          }
          table.delete(id)
        }
        case None => false
      }
    }

    def lookup(id: Long): Option[Address] = inTransaction {
      table.lookup(id) match {
        case None => None
        case Some(dao) => Some(dao.address)
      }
    }

    def insert(address: Address): AddressDAO = inTransaction {
      val locationDAO = locations.insert(LocationDAO(address.location))

      address match {
        case ua: UrbanAddress => {
          val dao = table.insert(new AddressDAO(locationDAO.id, AddressDAO.urbanTypeId))
          urbanAddresses.insert(UrbanAddressDAO(dao.id, ua))
          dao
        }
        case ca: CountrysideAddress => {
          val dao = table.insert(new AddressDAO(locationDAO.id, AddressDAO.countrysideTypeId))
          countrysideAddresses.insert(CountrysideAddressDAO(dao.id, ca))
          dao
        }
      }
    }

    def update(id: Long, address: Address): Boolean = inTransaction {
      table.lookup(id) match {
        case Some(dao) => {
          try {
            locations.update(LocationDAO(address.location, dao.locationId))
            address match {
              case ua: UrbanAddress => {
                if (dao.typeId != AddressDAO.urbanTypeId)
                  throw new Exception("Change of address type is not allowed")
                urbanAddresses.update(UrbanAddressDAO(dao.id, ua))
                true
              }
              case ca: CountrysideAddress => {
                if (dao.typeId != AddressDAO.countrysideTypeId)
                  throw new Exception("Change of address type is not allowed")
                countrysideAddresses.update(CountrysideAddressDAO(dao.id, ca))
                true
              }
            }
          } catch {
            case e: Exception => {
              false
            }
          }
        }
        case None => false
      }
    }
  }
}