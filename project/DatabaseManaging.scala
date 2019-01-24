import sbt._
import sbt.Keys._

object DatabaseManaging {
  def cleanupEvolutions = Command.args("cleanupEvolutions", "<databe's name>") { (state, args) =>
    def processName(name: String): String = "runMain EvolutionsCleanup " + name
    val names: Seq[String] = if (args.size > 0) args else Seq("")
    makeCommandsList(state, names)(processName _)
  }

  def createDdl = Command.args("createDdlAndEvolutions", "<databe's name>") { (state, args) =>
    def processName(name: String): String = "runMain DdlAndEvolutionsCreation " + name
    val names: Seq[String] = if (args.size > 0) args else Seq("")
    makeCommandsList(state, names)(processName _)
  }
  
  def dropEvolutions = Command.args("dropEvolutions", "<databe's name>") { (state, args) =>
    def processName(name: String): String = "runMain DroppingEvolutions " + name
    val names: Seq[String] = if (args.size > 0) args else Seq("")
    makeCommandsList(state, names)(processName _)
  }

  def dropDb = Command.args("dropDb", "<databe's name>") { (state, args) =>
    def processName(name: String): String = "runMain DroppingDatabase " + name
    val names: Seq[String] = if (args.size > 0) args else Seq("")
    makeCommandsList(state, names)(processName _)
  }

  def createDbExplicitly = Command.args("createDbExplicitly", "<databe's name>") { (state, args) =>
    def processName(name: String): String = "runMain ExplicitDatabaseCreationWithoutEvolutions " + name
    val names: Seq[String] = if (args.size > 0) args else Seq("")
    makeCommandsList(state, names)(processName _)
  }

  private def makeCommandsList(state: State, names: Seq[String])(processName: String => String) =
    names.foldRight(state) { (name, list) => processName(name) :: list }
}

