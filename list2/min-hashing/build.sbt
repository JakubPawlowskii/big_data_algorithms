val scala3Version = "3.2.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "min-hashing",
    fork := true,
    scalaVersion := scala3Version,
    javaOptions += "-Xmx8G",
    javaOptions += "-XX:+UseParallelGC",


  )
