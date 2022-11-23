val scala3Version = "3.2.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "edge-inversion",
    scalaVersion := scala3Version,
  )
