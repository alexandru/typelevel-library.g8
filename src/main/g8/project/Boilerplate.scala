import BuildKeys._

import com.github.tkawachi.doctest.DoctestPlugin.DoctestTestFramework
import com.github.tkawachi.doctest.DoctestPlugin.autoImport._
import sbt._
import sbt.Keys._
import sbtunidoc.BaseUnidocPlugin.autoImport.{unidoc, unidocProjectFilter}
import sbtunidoc.ScalaUnidocPlugin.autoImport.ScalaUnidoc

import scala.xml.Elem
import scala.xml.transform.{RewriteRule, RuleTransformer}

object Boilerplate {
  /**
    * Settings specific for Scoverage.
    */
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

  /**
    * For working with Scala version-specific source files, allowing us to
    * use 2.12 or 2.13 specific APIs.
    */
  lazy val crossVersionSharedSources: Seq[Setting[_]] = {
    def scalaPartV = Def setting (CrossVersion partialVersion scalaVersion.value)
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
  }

  /**
    * Skip publishing artifact for this project.
    */
  lazy val doNotPublishArtifact = Seq(
    skip in publish := true,
    publish := (()),
    publishLocal := (()),
    publishArtifact := false,
    publishTo := None
  )

  /**
    * Configures generated API documentation website.
    */
  def unidocSettings(projects: ProjectReference*) = Seq(
    // Only include JVM sub-projects, exclude JS or Native sub-projects
    unidocProjectFilter in (ScalaUnidoc, unidoc) := inProjects(projects:_*),

    scalacOptions in (ScalaUnidoc, unidoc) +=
      "-Xfatal-warnings",
    scalacOptions in (ScalaUnidoc, unidoc) --=
      Seq("-Ywarn-unused-import", "-Ywarn-unused:imports"),
    scalacOptions in (ScalaUnidoc, unidoc) ++=
      Opts.doc.title(projectTitle.value),
    scalacOptions in (ScalaUnidoc, unidoc) ++=
      Opts.doc.sourceUrl(s"https://github.com/\${githubFullRepositoryID.value}/tree/v\${version.value}€{FILE_PATH}.scala"),
    scalacOptions in (ScalaUnidoc, unidoc) ++=
      Seq("-doc-root-content", file("rootdoc.txt").getAbsolutePath),
    scalacOptions in (ScalaUnidoc, unidoc) ++=
      Opts.doc.version(version.value)
  )

  /**
    * Settings for `sbt-doctest`, for unit testing ScalaDoc.
    */
  def doctestTestSettings(tf: DoctestTestFramework) = Seq(
    doctestTestFramework := tf,
    doctestIgnoreRegex := Some(s".*(internal).*"),
    doctestOnlyCodeBlocksMode := true
  )
}
