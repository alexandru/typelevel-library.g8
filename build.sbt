val CatsVersion             = "2.6.1"
val CatsEffectVersion       = "3.2.7"
val MacroParadiseVersion    = "2.1.1"
val ScalaCheckVersion       = "1.15.4"
val KindProjectorVersion    = "0.13.2"
val BetterMonadicForVersion = "0.3.1"
val GitHub4sVersion         = "0.29.1"
val ScalaTestVersion        = "3.2.9"
val ScalaTestPlusVersion    = "3.2.9.0"

lazy val root = (project in file("."))
  .settings(
    scalaVersion := "2.12.14",
    crossScalaVersions := Seq("2.12.14", "2.13.11", "3.0.2"),

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
      "org.scalatestplus" %%% "scalacheck-1-15"  % ScalaTestPlusVersion % Test,
      "org.scalacheck"    %%% "scalacheck"       % ScalaCheckVersion    % Test,
      "org.typelevel"     %%% "cats-core"        % CatsVersion          % Test,
      "org.typelevel"     %%% "cats-effect"      % CatsEffectVersion    % Test,
      "org.typelevel"     %%% "cats-effect-laws" % CatsEffectVersion    % Test,
      "org.typelevel"     %%% "cats-laws"        % CatsVersion          % Test,
      "com.47deg"         %%% "github4s"         % GitHub4sVersion      % Test,
    ),
    libraryDependencies ++= (CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, _)) =>
        Seq(
          compilerPlugin(("org.typelevel"   % "kind-projector"     % KindProjectorVersion).cross(CrossVersion.full) % Test),
          compilerPlugin(("org.scalamacros" % "paradise"           % MacroParadiseVersion).cross(CrossVersion.patch) % Test),
          compilerPlugin("com.olegpy"      %% "better-monadic-for" % BetterMonadicForVersion % Test),
        )
      case _ =>
        Seq.empty
    }),
  )

