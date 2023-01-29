val scala3Version = "3.2.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "page-rank",
    scalaVersion := scala3Version,
    libraryDependencies += "org.jsoup" % "jsoup" % "1.14.2",
  
    
    scalacOptions ++= Seq(
      "-deprecation",
      "-explain")
    



  )
