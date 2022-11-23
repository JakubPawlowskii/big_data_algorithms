val scala3Version = "3.2.1"
val sparkVersion = "3.3.1"
lazy val root = project
  .in(file("."))
  .settings(
    name := "word-cloud",
    scalaVersion := scala3Version,
    scalacOptions ++= Seq(
      "-deprecation"
    )
  )
