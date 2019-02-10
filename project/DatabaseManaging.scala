import sbt._
import sbt.Keys._

import CommandsHelper._

object DatabaseManaging {
  def cleanupEvolutions = Command.args("cleanupEvolutions", "<databe's name>") { (state, args) =>
    def processName(name: String): String = "runMain database.EvolutionsCleanup " + name
    val names: Seq[String] = if (args.size > 0) args else Seq("")
    makeCommandsList(state, names)(processName _)
  }

  def createDdl = Command.args("createDdlAndEvolutions", "<databe's name>") { (state, args) =>
    def processName(name: String): String = "runMain database.DdlAndEvolutionsCreation " + name
    val names: Seq[String] = if (args.size > 0) args else Seq("")
    makeCommandsList(state, names)(processName _)
  }

  def dropEvolutions = Command.args("dropEvolutions", "<databe's name>") { (state, args) =>
    def processName(name: String): String = "runMain database.DroppingEvolutions " + name
    val names: Seq[String] = if (args.size > 0) args else Seq("")
    makeCommandsList(state, names)(processName _)
  }

  def dropDb = Command.args("dropDb", "<databe's name>") { (state, args) =>
    def processName(name: String): String = "runMain database.DroppingDatabase " + name
    val names: Seq[String] = if (args.size > 0) args else Seq("")
    makeCommandsList(state, names)(processName _)
  }

  def createDbExplicitly = Command.args("createDbExplicitly", "<databe's name>") { (state, args) =>
    def processName(name: String): String = "runMain database.ExplicitDatabaseCreationWithoutEvolutions " + name
    val names: Seq[String] = if (args.size > 0) args else Seq("")
    makeCommandsList(state, names)(processName _)
  }

  }
