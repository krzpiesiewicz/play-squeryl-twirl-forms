package main

import main.GlobalContext.injector
import play.api.i18n.{Langs, Lang}

object ScalaI18nService {
  private val langs = injector.getInstance(classOf[Langs])
  val availableLangs: Seq[Lang] = langs.availables
}