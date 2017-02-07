package calculator.routes

import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class RoutesSpec extends UnitSpec with WithFakeApplication {

  "The route for the welcome action on the home controller" should {
    "be /vat-flat-rate-calculator-frontend" in {
      calculator.controllers.routes.HomeController.welcome().url shouldBe "/vat-flat-rate-calculator-frontend"
    }
  }

}

