val scala3Version = "3.2.1"
val sparkVersion = "3.3.1"
lazy val root = project
  .in(file("."))
  .settings(
    name := "word-cloud",
    scalaVersion := scala3Version,
    scalacOptions ++= Seq(
      "-deprecation"
    ),
    libraryDependencies ++= Seq(
      ("io.github.vincenzobaz" %% "spark-scala3" % "0.1.3"),
      ("org.apache.spark" %% "spark-core" % sparkVersion)
        .cross(CrossVersion.for3Use2_13),
      ("org.apache.spark" %% "spark-sql" % sparkVersion)
        .cross(CrossVersion.for3Use2_13),
      ("org.apache.spark" %% "spark-mllib" % sparkVersion)
        .cross(CrossVersion.for2_13Use3)
    )
  )
