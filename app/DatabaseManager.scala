import java.sql.DriverManager
import java.io.StringWriter
import java.io.PrintWriter
import java.io.File
import java.lang.Exception

import org.squeryl.{ Session, SessionFactory }
import org.squeryl.adapters.SQLiteAdapter

import com.typesafe.config.ConfigFactory

import play.api.db.Databases
import play.api.db.evolutions.Evolutions
import play.api.Logger

import ArgsUtil._

class DatabaseManager(val dbName: String) {

  val conf = ConfigFactory.load()
  val dbPrefix = "db." + dbName
  val driver = conf.getString(dbPrefix + ".driver")
  val url = conf.getString(dbPrefix + ".url")

  val database = Databases(
    driver = driver,
    url = url,
    name = dbName)

  def cleanUpEvolutions() {
    //val s = createAndBindSession()
    //val c = database.getConnection()
    Evolutions.cleanupEvolutions(database)
  }
  
  def dropEvolutions() {
    val s = createAndBindSession()
    val conn = s.connection
    val stmt = conn.createStatement()
    stmt.execute("drop table if exists play_evolutions;")
  }

  def createAndBindSession(): Session = {
    val session = Session.create(database.getConnection(), new SQLiteAdapter)
    session.bindToCurrentThread
    session
  }
}

object ArgsUtil {
  def getDatabaseNameFromArgs(args: Array[String]): String =
    if (args.size == 0) "default" else args(0)
}

object EvolutionsCleanup {
  def main(args: Array[String]) {
    val name = getDatabaseNameFromArgs(args)
    Logger.info(s"Cleaning up evolutions for database '$name'")
    val dbManager = new DatabaseManager(name)
    dbManager.cleanUpEvolutions()
  }
}

object DroppingEvolutions {
  def main(args: Array[String]) {
    val name = getDatabaseNameFromArgs(args)
    Logger.info(s"Dropping table 'play_evolutions' in database '$name'")
    val dbManager = new DatabaseManager(name)
    dbManager.dropEvolutions()
  }
}

object DdlAndEvolutionsCreation {
  def main(args: Array[String]) {
    val name = getDatabaseNameFromArgs(args)
    val dbManager = new DatabaseManager(name)
    dbManager.createAndBindSession()

    try {
      Logger.info(s"Deleting the evolutions files for database '${dbManager.dbName}'.")
      val catalogPath = s"conf/evolutions/${dbManager.dbName}"
      val catalog = new File(catalogPath)
      if (catalog.exists()) {
        if (catalog.isDirectory)
          catalog.listFiles foreach { _.delete() }
        catalog.delete()
      }
      catalog.mkdir()
      Logger.info(s"Evolutions files deleted.");

      val path = s"$catalogPath/1.sql"
      val out = new File(path)
      val w = new PrintWriter(out);
      
      Logger.info(s"Creating DDL and evolutions for database '$name'")

      w.println("# --- !Ups")

      database.DBSchema.printDdl(w)

      w.println("\n# --- !Downs")

      database.DBSchema.tables foreach { t =>
        w.println(s"drop table ${t.name};")
      }

      w.println("""
      |-- The drop statements have been generated only for tables.
      |-- Remember to add appropriate drop statement for objects of different kinds (e.g. sequences).""".stripMargin)

      w.flush()
      w.close()

      Logger.info(s"DDL successfully written to '$path'. You should see it before applying the evolution.")

    } catch {
      case e: Exception => {
        Logger.error("ERROR!!!")
        throw e
      }
    }
  }
}

object DroppingDatabase {
  def main(args: Array[String]) {
    val name = getDatabaseNameFromArgs(args)
    val dbManager = new DatabaseManager(name)
    dbManager.createAndBindSession()
    Logger.info(s"Dropping database '$name'")
    database.DBSchema.drop
  }
}

object ExplicitDatabaseCreationWithoutEvolutions {
  def main(args: Array[String]) {
    val name = getDatabaseNameFromArgs(args)
    val dbManager = new DatabaseManager(name)
    dbManager.createAndBindSession()
    Logger.info(s"Droping database '$name'")
    database.DBSchema.drop
    Logger.info(s"Creating database '$name' by squeryl without evolutions")
    database.DBSchema.create
  }
}
