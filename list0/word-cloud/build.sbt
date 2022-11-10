val scala3Version = "3.2.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "word-cloud",
    scalaVersion := scala3Version,
    scalacOptions ++= Seq(
      "-deprecation"
    )
    // libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test
  )
