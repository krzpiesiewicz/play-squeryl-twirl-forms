import sbt._
import sbt.Keys._

object CommandsHelper {
  def makeCommandsList(state: State, names: Seq[String])(processName: String => String) =
    names.foldRight(state) { (name, list) => processName(name) :: list }
}
