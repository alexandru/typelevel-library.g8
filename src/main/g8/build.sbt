// shadow sbt-scalajs' crossProject and CrossType from Scala.js 0.6.x
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}
// For getting Scoverage out of the generated POM
import scala.xml.Elem
import scala.xml.transform.{RewriteRule, RuleTransformer}

/**
  * Standard FP library for Scala:
  * [[https://typelevel.org/cats/]]
  */
val CatsVersion = "$cats_version$"
/**
  * FP library for describing side-effects:
  * [[https://typelevel.org/cats-effect/]]
  */
val CatsEffectVersion = "$cats_effect_version$"
/**
  * Library for unit-testing:
  * [[https://github.com/monix/minitest/]]
  */
val MinitestVersion = "$minitest_version$"
/**
  * Library for property-based testing:
  * [[https://www.scalacheck.org/]]
  */
val ScalaCheckVersion = "$scalacheck_version$"
/**
  * Compiler plugin for working with partially applied types:
  * [[https://github.com/typelevel/kind-projector]]
  */
val KindProjectorVersion = "0.11.0"
/**
  * Compiler plugin for fixing "for comprehensions" to do desugaring w/o `withFilter`:
  * [[https://github.com/typelevel/kind-projector]]
  */
val BetterMonadicForVersion = "0.3.1"

/** For parsing git tags for determining version number. */
val ReleaseTag = """^v(\d+\.\d+(?:\.\d+(?:[-.]\w+)?)?)\$""".r

/**
  * For specifying the project's repository ID.
  *
  * Examples:
  *
  *  - typelevel/cats
  *  - typelevel/cats-effect
  *  - monix/monix
  */
lazy val gitHubRepositoryID =
  settingKey[String]("GitHub repository ID (e.g. user_id/project_name)")

/**
  * Folder where the API docs will be uploaded when generating site.
  */
lazy val docsMappingsAPIDir =
  settingKey[String]("Name of subdirectory in site target directory for api docs")

def profile: Project ⇒ Project = pr => {
  val withCoverage = sys.env.getOrElse("SBT_PROFILE", "") match {
    case "coverage" => pr
    case _ => pr.disablePlugins(scoverage.ScoverageSbtPlugin)
  }
  withCoverage
    .enablePlugins(AutomateHeaderPlugin)
    .enablePlugins(GitVersioning)
}

def scalaPartV = Def setting (CrossVersion partialVersion scalaVersion.value)
lazy val crossVersionSharedSources: Seq[Setting[_]] =
  Seq(Compile, Test).map { sc =>
    (unmanagedSourceDirectories in sc) ++= {
      (unmanagedSourceDirectories in sc).value.flatMap { dir =>
        Seq(
          scalaPartV.value match {
            case Some((2, y)) if y == 11 => new File(dir.getPath + "_2.11")
            case Some((2, y)) if y == 12 => new File(dir.getPath + "_2.12")
            case Some((2, y)) if y >= 13 => new File(dir.getPath + "_2.13")
          },

          scalaPartV.value match {
            case Some((2, n)) if n >= 12 => new File(dir.getPath + "_2.12+")
            case _                       => new File(dir.getPath + "_2.12-")
          },

          scalaPartV.value match {
            case Some((2, n)) if n >= 13 => new File(dir.getPath + "_2.13+")
            case _                       => new File(dir.getPath + "_2.13-")
          },
        )
      }
    }
  }

lazy val coverageSettings = Seq(
  // For evicting Scoverage out of the generated POM
  // See: https://github.com/scoverage/sbt-scoverage/issues/153
  pomPostProcess := { (node: xml.Node) =>
    new RuleTransformer(new RewriteRule {
      override def transform(node: xml.Node): Seq[xml.Node] = node match {
        case e: Elem
          if e.label == "dependency" && e.child.exists(child => child.label == "groupId" && child.text == "org.scoverage") => Nil
        case _ => Seq(node)
      }
    }).transform(node).head
  },
)

lazy val doNotPublishArtifact = Seq(
  publishArtifact := false,
  publishArtifact in (Compile, packageDoc) := false,
  publishArtifact in (Compile, packageSrc) := false,
  publishArtifact in (Compile, packageBin) := false
)

lazy val sharedJSSettings = Seq(
  coverageExcludedFiles := ".*",
  // Use globally accessible (rather than local) source paths in JS source maps
  scalacOptions += {
    val tagOrHash =
      if (isSnapshot.value) git.gitHeadCommit.value.get
      else s"v\${git.baseVersion.value}"
    val l = (baseDirectory in LocalRootProject).value.toURI.toString
    val g = s"https://raw.githubusercontent.com/\${gitHubRepositoryID.value}/\$tagOrHash/"
    s"-P:scalajs:mapSourceURI:\$l->\$g"
  }
)

lazy val unidocSettings = Seq(
  // Only include JVM sub-projects, exclude JS or Native sub-projects
  unidocProjectFilter in (ScalaUnidoc, unidoc) :=
    inProjects($sub_project_id$JVM),

  scalacOptions in (ScalaUnidoc, unidoc) +=
    "-Xfatal-warnings",
  scalacOptions in (ScalaUnidoc, unidoc) --=
    Seq("-Ywarn-unused-import", "-Ywarn-unused:imports"),
  scalacOptions in (ScalaUnidoc, unidoc) ++=
    Opts.doc.title(s"$name$"),
  scalacOptions in (ScalaUnidoc, unidoc) ++=
    Opts.doc.sourceUrl(s"https://github.com/$github_user_id$/$github_repository_name$/tree/v\${version.value}€{FILE_PATH}.scala"),
  scalacOptions in (ScalaUnidoc, unidoc) ++=
    Seq("-doc-root-content", file("rootdoc.txt").getAbsolutePath),
  scalacOptions in (ScalaUnidoc, unidoc) ++=
    Opts.doc.version(s"\${version.value}")
)

lazy val doctestTestSettings = Seq(
  doctestTestFramework := DoctestTestFramework.Minitest,
  doctestIgnoreRegex := Some(s".*(internal).*"),
  doctestOnlyCodeBlocksMode := true
)

lazy val sharedSettings = Seq(
  gitHubRepositoryID := "$github_user_id$/$github_repository_name$",
  organization := "$organization$",
  scalaVersion := "2.13.1",
  crossScalaVersions := Seq("2.12.10", "2.13.1"),

  scalacOptions ++= Seq(
    // warnings
    "-unchecked", // able additional warnings where generated code depends on assumptions
    "-deprecation", // emit warning for usages of deprecated APIs
    "-feature", // emit warning usages of features that should be imported explicitly
    // Features enabled by default
    "-language:higherKinds",
    "-language:implicitConversions",
    "-language:experimental.macros",
  ),

  // Linter options
  scalacOptions ++= Seq(
    "-Xfatal-warnings", // Turns all warnings into errors ;-)
    "-Xlint:adapted-args", // warn if an argument list is modified to match the receiver
    "-Xlint:nullary-unit", // warn when nullary methods return Unit
    "-Xlint:nullary-override", // warn when non-nullary `def f()' overrides nullary `def f'
    "-Xlint:infer-any", // warn when a type argument is inferred to be `Any`
    "-Xlint:missing-interpolator", // a string literal appears to be missing an interpolator id
    "-Xlint:doc-detached", // a ScalaDoc comment appears to be detached from its element
    "-Xlint:private-shadow", // a private field (or class parameter) shadows a superclass field
    "-Xlint:type-parameter-shadow", // a local type parameter shadows a type already in scope
    "-Xlint:poly-implicit-overload", // parameterized overloaded implicit methods are not visible as view bounds
    "-Xlint:option-implicit", // Option.apply used implicit view
    "-Xlint:delayedinit-select", // Selecting member of DelayedInit
    "-Xlint:package-object-classes", // Class or object defined in package object
  ),
  scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, majorVersion)) if majorVersion <= 12 =>
      Seq(
        "-Xlint:inaccessible", // warn about inaccessible types in method signatures
        "-Xlint:by-name-right-associative", // By-name parameter of right associative operator
        "-Xlint:unsound-match" // Pattern match may not be typesafe
      )
    case _ =>
      Seq.empty
  }),
  // Turning off fatal warnings for ScalaDoc, otherwise we can't release.
  scalacOptions in (Compile, doc) ~= (_ filterNot (_ == "-Xfatal-warnings")),

  // More version specific compiler options
  scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, v)) if v <= 12 =>
      Seq(
        "-Ypartial-unification",
      )
    case _ =>
      Seq(
        // Replaces macro-paradise in Scala 2.13
        "-Ymacro-annotations",
      )
  }),

  addCompilerPlugin("org.typelevel" % "kind-projector" % KindProjectorVersion cross CrossVersion.full),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % BetterMonadicForVersion),

  // ScalaDoc settings
  autoAPIMappings := true,
  scalacOptions in ThisBuild ++= Seq(
    // Note, this is used by the doc-source-url feature to determine the
    // relative path of a given source file. If it's not a prefix of a the
    // absolute path of the source file, the absolute path of that file
    // will be put into the FILE_SOURCE variable, which is
    // definitely not what we want.
    "-sourcepath", file(".").getAbsolutePath.replaceAll("[.]\$", "")
  ),

  // https://github.com/sbt/sbt/issues/2654
  incOptions := incOptions.value.withLogRecompileOnMacro(false),

  // ---------------------------------------------------------------------------
  // Source directories (also see `crossVersionSharedSources`)

  unmanagedSourceDirectories in Compile += {
    baseDirectory.value.getParentFile / "shared" / "src" / "main" / "scala"
  },
  unmanagedSourceDirectories in Test += {
    baseDirectory.value.getParentFile / "shared" / "src" / "test" / "scala"
  },

  // ---------------------------------------------------------------------------
  // Options for testing

  testFrameworks += new TestFramework("minitest.runner.Framework"),
  logBuffered in Test := false,
  logBuffered in IntegrationTest := false,
  parallelExecution in Test := false,

  // ---------------------------------------------------------------------------
  // Options meant for publishing on Maven Central

  publishMavenStyle := true,
  publishTo := Some(
    if (isSnapshot.value)
      Opts.resolver.sonatypeSnapshots
    else
      Opts.resolver.sonatypeStaging
  ),

  isSnapshot := version.value endsWith "SNAPSHOT",
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false }, // removes optional dependencies

  licenses := Seq("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
  homepage := Some(url(s"$homepage_url$")),
  headerLicense := Some(HeaderLicense.Custom(
    """|Copyright (c) 2020 the $name$ contributors.
       |See the project homepage at: $homepage_url$
       |
       |Licensed under the Apache License, Version 2.0 (the "License");
       |you may not use this file except in compliance with the License.
       |You may obtain a copy of the License at
       |
       |    http://www.apache.org/licenses/LICENSE-2.0
       |
       |Unless required by applicable law or agreed to in writing, software
       |distributed under the License is distributed on an "AS IS" BASIS,
       |WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
       |See the License for the specific language governing permissions and
       |limitations under the License."""
    .stripMargin)),

  scmInfo := Some(
    ScmInfo(
      url(s"https://github.com/\${gitHubRepositoryID.value}"),
      s"scm:git@github.com:\${gitHubRepositoryID.value}.git"
    )),

  developers := List(
    Developer(
      id="$sonatype_developer_id$",
      name="$developer_name$",
      email="$developer_email$",
      url=url("$developer_website$")
    )),

  // -- Settings meant for deployment on oss.sonatype.org
  sonatypeProfileName := organization.value,

  // ---------------------------------------------------------------------------
  // Versioning settings — based on Git

  git.baseVersion := "0.1.0",

  git.gitTagToVersionNumber := {
    case ReleaseTag(v) => Some(v)
    case _ => None
  },

  git.formattedShaVersion := {
    val suffix = git.makeUncommittedSignifierSuffix(git.gitUncommittedChanges.value, git.uncommittedSignifier.value)

    git.gitHeadCommit.value map { _.substring(0, 7) } map { sha =>
      git.baseVersion.value + "-" + sha + suffix
    }
  }
)

lazy val root = project.in(file("."))
  .enablePlugins(ScalaUnidocPlugin)
  .aggregate($sub_project_id$JVM, $sub_project_id$JS)
  .configure(profile)
  .settings(sharedSettings)
  .settings(doNotPublishArtifact)
  .settings(unidocSettings)

lazy val site = project.in(file("site"))
  .disablePlugins(MimaPlugin)
  .enablePlugins(MicrositesPlugin)
  .enablePlugins(MdocPlugin)
  .settings(sharedSettings)
  .settings(doNotPublishArtifact)
  .dependsOn($sub_project_id$JVM)
  .settings{
    import microsites._
    Seq(
      micrositeName := "$name$",
      micrositeDescription := "$project_description$",
      micrositeAuthor := "$developer_name$",
      micrositeTwitterCreator := "@$developer_twitter_id$",
      micrositeGithubOwner := "$github_user_id$",
      micrositeGithubRepo := "$github_repository_name$",
      micrositeUrl := "https://$microsite_domain$",
      micrositeBaseUrl := "$microsite_base_url$",
      micrositeDocumentationUrl := "https://$microsite_domain$$microsite_base_url$api/",
      micrositeGitterChannelUrl := "$github_user_id$/$github_repository_name$",
      micrositeFooterText := None,
      micrositeHighlightTheme := "atom-one-light",
      micrositePalette := Map(
        "brand-primary" -> "#3e5b95",
        "brand-secondary" -> "#294066",
        "brand-tertiary" -> "#2d5799",
        "gray-dark" -> "#49494B",
        "gray" -> "#7B7B7E",
        "gray-light" -> "#E5E5E6",
        "gray-lighter" -> "#F4F3F4",
        "white-color" -> "#FFFFFF"
      ),
      micrositeCompilingDocsTool := WithMdoc,
      scalacOptions in Tut --= Seq(
        "-Xfatal-warnings",
        "-Ywarn-unused-import",
        "-Ywarn-numeric-widen",
        "-Ywarn-dead-code",
        "-Ywarn-unused:imports",
        "-Xlint:-missing-interpolator,_"
      ),
      libraryDependencies += "com.47deg" %% "github4s" % "0.21.0",
      micrositePushSiteWith := GitHub4s,
      micrositeGithubToken := sys.env.get("GITHUB_TOKEN"),
      micrositeExtraMdFiles := Map(
        file("CODE_OF_CONDUCT.md") -> ExtraMdFileConfig("CODE_OF_CONDUCT.md", "page", Map("title" -> "Code of Conduct",   "section" -> "code of conduct", "position" -> "100")),
        file("LICENSE.md") -> ExtraMdFileConfig("LICENSE.md", "page", Map("title" -> "License",   "section" -> "license",   "position" -> "101"))
      ),
      docsMappingsAPIDir := "api",
      addMappingsToSiteDir(mappings in packageDoc in Compile in $sub_project_id$JVM, docsMappingsAPIDir)
    )
  }

lazy val $sub_project_id$ = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .in(file("$sub_project_id$"))
  .configure(profile)
  .settings(sharedSettings)
  .settings(crossVersionSharedSources)
  .settings(coverageSettings)
  .jvmSettings(doctestTestSettings)
  .jsSettings(sharedJSSettings)
  .settings(
    name := "$artifact_id$",
    libraryDependencies ++= Seq(
      "org.typelevel"  %%% "cats-core"        % CatsVersion,
      "org.typelevel"  %%% "cats-effect"      % CatsEffectVersion,
      // For testing
      "io.monix"       %%% "minitest"         % MinitestVersion % Test,
      "io.monix"       %%% "minitest-laws"    % MinitestVersion % Test,
      "org.scalacheck" %%% "scalacheck"       % ScalaCheckVersion % Test,
      "org.typelevel"  %%% "cats-laws"        % CatsVersion % Test,
      "org.typelevel"  %%% "cats-effect-laws" % CatsEffectVersion % Test,
    ),
  )

lazy val $sub_project_id$JVM = $sub_project_id$.jvm
lazy val $sub_project_id$JS  = $sub_project_id$.js

// Reloads build.sbt changes whenever detected
Global / onChangedBuildSource := ReloadOnSourceChanges
