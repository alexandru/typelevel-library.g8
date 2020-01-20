/** [[https://monix.io]] */
val MonixVersion = "$monix_version$"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.10",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "$name$",
    libraryDependencies ++= Seq(
      "io.monix" %% "monix" % MonixVersion
    )
  )
