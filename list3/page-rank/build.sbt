val scala3Version = "3.2.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "page-rank",
    scalaVersion := scala3Version,

    libraryDependencies ++= Seq("org.jsoup" % "jsoup" % "1.14.2",
                            "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4"),
    scalacOptions ++= Seq(
      "-deprecation",
      "-explain")
    
  )
