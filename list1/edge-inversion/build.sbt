val scala3Version = "3.2.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "edge-inversion",
    scalaVersion := scala3Version,
    scalacOptions ++= Seq(
      "-deprecation"
    ),
    // libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4",
  )
