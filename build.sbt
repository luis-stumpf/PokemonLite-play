ThisBuild / scalaVersion := "2.13.12"

ThisBuild / version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala).enablePlugins(SbtWeb)
  .settings(
    name := """PokemonLite-play""",
    Compile / scalacOptions += "-Ytasty-reader",
    libraryDependencies ++= Seq(
        guice,
        "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
        "org.playframework" %% "play-json" % "3.0.1"
    )
  )