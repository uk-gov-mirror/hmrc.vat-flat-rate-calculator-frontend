import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt._

object FrontendBuild extends Build with MicroService {

  val appName = "vat-flat-rate-calculator-frontend"

  override lazy val appDependencies: Seq[ModuleID] = AppDependencies()
}

  private object AppDependencies {

  val compile = Seq(
    ws,

    "uk.gov.hmrc" %% "bootstrap-play-26" % "0.39.0",
    "uk.gov.hmrc" %% "play-partials" % "6.9.0-play-26",
    "uk.gov.hmrc" %% "http-caching-client" % "8.3.0",
    "uk.gov.hmrc" %% "govuk-template" % "5.34.0-play-26",
    "uk.gov.hmrc" %% "play-ui" % "7.37.0-play-26"
  )

    trait TestDependencies {
      lazy val scope: String = "test"
      lazy val test: Seq[ModuleID] = ???
    }

    object Test {
      def apply(): Seq[ModuleID] = new TestDependencies {
        override lazy val test = Seq(
          "uk.gov.hmrc" %% "hmrctest" % "3.8.0-play-26" % scope,
          "org.scalatest" %% "scalatest" % "2.2.6" % scope,
          "org.pegdown" % "pegdown" % "1.6.0" % scope,
          "org.jsoup" % "jsoup" % "1.8.3" % scope,
          "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
          "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0-M1" % scope,
          "org.mockito" % "mockito-core" % "2.6.2" % "test"
        )
      }.test
    }

    def apply(): Seq[ModuleID] = compile ++ Test()

}
