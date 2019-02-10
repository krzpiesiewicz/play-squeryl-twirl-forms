name := """play-and-squeryl"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.6"

routesGenerator := play.routes.compiler.InjectedRoutesGenerator

// for functional programming:
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.27"

// for reflection:
libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value

// for H2 database
// libraryDependencies += "com.h2database" % "h2" % "1.2.127"

// for the dependency injection:
libraryDependencies += guice

// for wiring objects:
val macwireVersion = "2.3.1"
val macwireMacros = "com.softwaremill.macwire" %% "macros" % macwireVersion % Provided
val macwireUtil = "com.softwaremill.macwire" %% "util" % macwireVersion
val macwireProxy = "com.softwaremill.macwire" %% "proxy" % macwireVersion
libraryDependencies ++= Seq(macwireMacros, macwireUtil, macwireProxy)

// for testing:
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

// low-level database binding for higher levels:
libraryDependencies += jdbc

// for using SQLITE databases:
// https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc
libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.25.2"

// database ORM:
// https://mvnrepository.com/artifact/org.squeryl/squeryl
libraryDependencies += "org.squeryl" %% "squeryl" % "0.9.12"

// for database migrations:
libraryDependencies += evolutions
// https://mvnrepository.com/artifact/com.typesafe.play/play-jdbc-evolutions
libraryDependencies += "com.typesafe.play" %% "play-jdbc-evolutions" % "2.7.0-M4" % Test

// adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// adds additional packages into conf/routes
//play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"

// Automatic Case Class Mappings (via runtime reflection):
libraryDependencies += "com.iterable" %% "iterableplayutils" % "2.0.0"

import DatabaseManaging._
import CreatingExampleData._
commands ++= Seq(cleanupEvolutions, dropEvolutions, dropDb, createDdl, createDbExplicitly, createExampleData)

PlayKeys.devSettings ++= Seq("play.server.http.port" -> "9000")
