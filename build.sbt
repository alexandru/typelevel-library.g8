val CatsVersion             = "2.7.0"
val ScalaCheckVersion       = "1.16.0"
val KindProjectorVersion    = "0.13.2"
val GitHub4sVersion         = "0.31.0"
val ScalaTestVersion        = "3.2.12"
val ScalaTestPlusVersion    = "3.2.12.0"

lazy val root = (project in file("."))
  .settings(
    scalaVersion := "2.12.15",
    crossScalaVersions := Seq("2.12.15", "2.13.8", "3.1.2"),

    Test / test := {
      val _ = (Test / g8Test).toTask("").value
    },
    scriptedLaunchOpts ++= List("-Xms1024m", "-Xmx1024m", "-XX:ReservedCodeCacheSize=128m", "-XX:MaxPermSize=256m", "-Xss2m", "-Dfile.encoding=UTF-8"),
    resolvers += Resolver.url("typesafe", url("https://repo.typesafe.com/typesafe/ivy-releases/"))(Resolver.ivyStylePatterns),
    Global / onChangedBuildSource := ReloadOnSourceChanges,

    // Adding dependencies in order to force Scala Steward to help us
    // update the g8 template as well
    libraryDependencies ++= Seq(
      "org.scalatest"     %%% "scalatest"        % ScalaTestVersion     % Test,
      "org.scalatestplus" %%% "scalacheck-1-16"  % ScalaTestPlusVersion % Test,
      "org.scalacheck"    %%% "scalacheck"       % ScalaCheckVersion    % Test,
      "org.typelevel"     %%% "cats-core"        % CatsVersion          % Test,
      "com.47deg"         %%% "github4s"         % GitHub4sVersion      % Test,
    ),
    libraryDependencies ++= (CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, _)) =>
        Seq(
          compilerPlugin(("org.typelevel"   % "kind-projector"     % KindProjectorVersion).cross(CrossVersion.full) % Test),
        )
      case _ =>
        Seq.empty
    }),
  )

