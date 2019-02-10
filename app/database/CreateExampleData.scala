package database

import org.squeryl.SessionFactory
import play.api.Logger

import database.CustomTypeMode._
import database.{ DBSchema => schema }
import models._

object CreateExampleData {

  def main(args: Array[String]) {

    val name = ArgsUtil.getDatabaseNameFromArgs(args)

    val dbManager = new DatabaseManager(name)

    SessionFactory.concreteFactory = Some(() => {
      dbManager.createAndBindSession()
    })

    Logger.info(s"Creating example data for database '$name'")

    transaction {
      val addresses = List(
        UrbanAddress("Poland", "mazowieckie", "Warszawa", "ul. Stefana Banacha", "2", None, Location(52.2117529, 20.9812973)),
        UrbanAddress("Poland", "mazowieckie", "Warszawa", "ul. Ludwika Pasteura", "7", None, Location(52.211805, 20.982751)),
        UrbanAddress("Poland", "mazowieckie", "Warszawa", "ul. Ratuszowa", "1", Some(3), Location(52.2525177, 21.0078917)),
        UrbanAddress("Poland", "małopolskie", "Kraków", "ul. Wawel", "5", None, Location(50.0523334,19.9527663)),
        UrbanAddress("Poland", "małopolskie", "Kraków", "pl. Mariacki", "5", None, Location(50.0621583,19.9454559)),
        UrbanAddress("Poland", "małopolskie", "Kraków", "ul. Śliczna ", "32a", Some(8), Location(52.2525177, 21.0078917)),
        CountrysideAddress("Poland", "małopolskie", "Nowa Wieś", "70", Location(50.2347606,19.8931922)),
        CountrysideAddress("Poland", "małopolskie", "Minoga", "122", Location(50.2347606,19.8931922)),
        UrbanAddress("Germany", "Berlin", "Berlin", "Invalidenstraße", "131", None, Location(52.5323402,13.3861044)))

      for (address <- addresses)
        schema.addresses.insert(address)
    }
  }
}