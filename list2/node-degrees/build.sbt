val scala3Version = "3.2.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "node-degrees",
    scalaVersion := scala3Version,
    scalacOptions ++= "-deprecation" :: "-explain" :: Nil,

  )
