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
    "uk.gov.hmrc" %% "frontend-bootstrap" % "10.7.0",
    "uk.gov.hmrc" %% "play-partials" % "6.1.0",
    "uk.gov.hmrc" %% "http-caching-client" % "7.2.0"
  )

    trait TestDependencies {
      lazy val scope: String = "test"
      lazy val test: Seq[ModuleID] = ???
    }

    object Test {
      def apply(): Seq[ModuleID] = new TestDependencies {
        override lazy val test = Seq(
          "uk.gov.hmrc" %% "hmrctest" % "3.2.0" % scope,
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
