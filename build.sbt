import org.scalajs.linker.interface.ModuleInitializer


val webapp = project
  .in(file("."))
  .dependsOn(shared)
  .settings(
    libraryDependencies ++= List(
      "com.raquo" %%% "laminar" % "0.13.1",
      "com.raquo" %%% "waypoint" % "0.4.0",
      "io.laminext" %%% "websocket" % "0.13.1",
      "io.circe" %%% "circe-parser" % "0.15.0-M1",
      "io.circe" %%% "circe-generic" % "0.15.0-M1",
      ("org.typelevel" %%% "squants" % "1.6.0").cross(CrossVersion.for3Use2_13),
    ),
    libraryDependencies ++= List(
      "com.github.torrentdam.server" %%% "protocol" % "1.0.1",
    ),
    Compile / scalaJSModuleInitializers ++= List(
      ModuleInitializer.mainMethod("default.Main", "init").withModuleID("main"),
    )
  )
  .settings(commonSettings)
  .enablePlugins(ScalaJSPlugin)
  .aggregate(sw)

lazy val sw = project
  .dependsOn(shared)
  .settings(
    Compile / scalaJSModuleInitializers ++= List(
      ModuleInitializer.mainMethod("default.ServiceWorker", "init").withModuleID("sw"),
    )
  )
  .settings(commonSettings)
  .enablePlugins(ScalaJSPlugin)

lazy val shared = project
  .settings(
    libraryDependencies ++= List(
      "com.github.torrentdam" %%% "common" % "1.0.0",
      "io.circe" %%% "circe-parser" % "0.15.0-M1",
      "io.circe" %%% "circe-generic" % "0.15.0-M1",
      ("org.scala-js" %%% "scalajs-dom" % "1.1.0").cross(CrossVersion.for3Use2_13),
    ),
    Compile / scalaJSModuleInitializers ++= List(
      ModuleInitializer.mainMethod("default.ServiceWorker", "init").withModuleID("sw"),
    )
  )
  .settings(commonSettings)
  .enablePlugins(ScalaJSPlugin)

lazy val commonSettings = List(
  organization := "com.github.lavrov",
  scalaVersion := "3.0.1",
  externalResolvers ++= List(
    "server packages" at "https://maven.pkg.github.com/TorrentDamDev/server",
    "bittorrent packages" at "https://maven.pkg.github.com/TorrentDamDev/bittorrent",
  )
)