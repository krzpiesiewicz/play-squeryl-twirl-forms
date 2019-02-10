import sbt._
import sbt.Keys._

import CommandsHelper._

object CreatingExampleData {
  def createExampleData = Command.args("createExampleData", "<databe's name>") { (state, args) =>
    def processName(name: String): String = "runMain database.CreateExampleData " + name
    val names: Seq[String] = if (args.size > 0) args else Seq("")
    makeCommandsList(state, names)(processName _)
  }
}
