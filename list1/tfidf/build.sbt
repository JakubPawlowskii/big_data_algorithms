val scala3Version = "3.2.1"
lazy val root = project
  .in(file("."))
  .settings(
    name := "tfidf",
    scalaVersion := scala3Version,
    scalacOptions ++= Seq(
      "-deprecation"
    ),
  )
