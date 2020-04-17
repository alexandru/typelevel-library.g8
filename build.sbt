val CatsVersion             = "2.1.1"
val CatsEffectVersion       = "2.1.3"
val NewtypeVersion          = "0.4.3"
val SimulacrumVersion       = "1.0.0"
val MacroParadiseVersion    = "2.1.0"
val MinitestVersion         = "2.8.2"
val ScalaCheckVersion       = "1.14.3"
val KindProjectorVersion    = "0.11.0"
val BetterMonadicForVersion = "0.3.1"
val SilencerVersion         = "1.6.0"
val GitHub4sVersion         = "0.21.0"

lazy val root = (project in file("."))
  .settings(
    scalaVersion := "2.12.10",
    crossScalaVersions := Seq("2.12.10", "2.13.1"),

    test in Test := {
      val _ = (g8Test in Test).toTask("").value
    },
    scriptedLaunchOpts ++= List("-Xms1024m", "-Xmx1024m", "-XX:ReservedCodeCacheSize=128m", "-XX:MaxPermSize=256m", "-Xss2m", "-Dfile.encoding=UTF-8"),
    resolvers += Resolver.url("typesafe", url("https://repo.typesafe.com/typesafe/ivy-releases/"))(Resolver.ivyStylePatterns),
    Global / onChangedBuildSource := ReloadOnSourceChanges,

    // Adding dependencies in order to force Scala Steward to help us
    // update the g8 template as well
    libraryDependencies ++= Seq(
      "io.estatico"    %%% "newtype"          % NewtypeVersion    % Test,
      "io.monix"       %%% "minitest"         % MinitestVersion   % Test,
      "io.monix"       %%% "minitest-laws"    % MinitestVersion   % Test,
      "org.scalacheck" %%% "scalacheck"       % ScalaCheckVersion % Test,
      "org.typelevel"  %%% "cats-core"        % CatsVersion       % Test,
      "org.typelevel"  %%% "cats-effect"      % CatsEffectVersion % Test,
      "org.typelevel"  %%% "cats-effect-laws" % CatsEffectVersion % Test,
      "org.typelevel"  %%% "cats-laws"        % CatsVersion       % Test,
      "org.typelevel"  %%% "simulacrum"       % SimulacrumVersion % Test,
      "com.47deg"      %%% "github4s"         % GitHub4sVersion   % Test,

      compilerPlugin(("org.typelevel"   % "kind-projector"     % KindProjectorVersion).cross(CrossVersion.full) % Test),
      compilerPlugin(("com.github.ghik" % "silencer-plugin"    % SilencerVersion).cross(CrossVersion.full) % Test),
      compilerPlugin(("org.scalamacros" % "paradise"           % MacroParadiseVersion).cross(CrossVersion.patch) % Test),
      compilerPlugin("com.olegpy"      %% "better-monadic-for" % BetterMonadicForVersion % Test),
    ),
  )

