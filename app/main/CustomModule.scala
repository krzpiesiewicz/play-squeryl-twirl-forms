package main

import org.squeryl.{ Session, SessionFactory }
import org.squeryl.adapters.SQLiteAdapter
import com.google.inject._

@Singleton
class SquerylInitialization @Inject() (db: play.api.db.Database) {
  SessionFactory.concreteFactory = Some(() => {
    val session = Session.create(db.getConnection(), new SQLiteAdapter)
    session.bindToCurrentThread
    session
  })
}

@Singleton
class GlobalContext @Inject() (injector: Injector) {
  GlobalContext.injector = injector
}

object GlobalContext {
  var injector: Injector = null
}

class CustomModule extends AbstractModule {
  override def configure() = {
    bind(classOf[SquerylInitialization]).asEagerSingleton()
    bind(classOf[GlobalContext]).asEagerSingleton()
  }
}
