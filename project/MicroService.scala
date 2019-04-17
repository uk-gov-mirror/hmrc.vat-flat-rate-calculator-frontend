import sbt.Keys._
import sbt._
import play.routes.compiler.InjectedRoutesGenerator
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin._
import com.typesafe.sbt.digest.Import.digest
import com.typesafe.sbt.web.Import.pipelineStages
import com.typesafe.sbt.web.Import.Assets
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion
import com.timushev.sbt.updates.UpdatesKeys._
import com.timushev.sbt.updates.UpdatesPlugin.autoImport.moduleFilterRemoveValue

trait MicroService {

  import uk.gov.hmrc._
  import DefaultBuildSettings._
  import uk.gov.hmrc.SbtAutoBuildPlugin
  import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
  import uk.gov.hmrc.versioning.SbtGitVersioning
  import play.sbt.routes.RoutesKeys.routesGenerator

  val appName: String

  lazy val appDependencies : Seq[ModuleID] = ???
  lazy val plugins : Seq[Plugins] = Seq.empty
  lazy val playSettings : Seq[Setting[_]] = Seq.empty

  lazy val scoverageSettings = {
    import scoverage.ScoverageKeys
    Seq(
      ScoverageKeys.coverageExcludedPackages := "<empty>;Reverse.*;models/.data/..*;" +
        "filters.*;.handlers.*;components.*;.*BuildInfo.*;.*FrontendAuditConnector.*;.*Routes.*;views.html.*;config.*;",
      ScoverageKeys.coverageMinimum := 90,
      ScoverageKeys.coverageFailOnMinimum := false,
      ScoverageKeys.coverageHighlighting := true
    )
  }

  lazy val microservice: Project = Project(appName, file("."))
    .enablePlugins(
      Seq(
        play.sbt.PlayScala,
        SbtAutoBuildPlugin,
        SbtGitVersioning,
        SbtDistributablesPlugin,
        SbtArtifactory
      ) ++ plugins : _*)
    .settings(playSettings : _*)
    .settings(scoverageSettings: _*)
    .settings(scalaSettings: _*)
    .settings(publishingSettings: _*)
    .settings(defaultSettings(): _*)
    .settings(majorVersion := 0 )
    .settings(
      libraryDependencies ++= appDependencies,
      retrieveManaged := true,
      evictionWarningOptions in update := EvictionWarningOptions.default.withWarnScalaVersionEviction(false),
      routesGenerator := InjectedRoutesGenerator,
      pipelineStages in Assets := Seq(digest),
      scalaVersion := "2.11.12"
    )
    .configs(IntegrationTest)
    .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
    .settings(integrationTestSettings())
    .settings(dependencyUpdatesFilter -= moduleFilter(organization = "org.scala-lang"))
    .settings(dependencyUpdatesFilter -= moduleFilter(organization = "com.typesafe.play"))
    .settings(dependencyUpdatesFilter -= moduleFilter(organization = "org.scalatest"))
    .settings(dependencyUpdatesFilter -= moduleFilter(organization = "org.scalatestplus.play"))
    .settings(dependencyUpdatesFailBuild := true)
}
