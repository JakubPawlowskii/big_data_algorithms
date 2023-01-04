lazy val root = (project in file(".")).settings(
  inThisBuild(
    List(
      scalaVersion := "2.13.8",
    )
  ),
  name := "spark-cloud",
  scalacOptions += "-deprecation",
  libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.3.1",
)
