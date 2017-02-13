/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package routes

import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class RoutesSpec extends UnitSpec with WithFakeApplication {
  val baseUrl = "/check-your-vat-flat-rate"

  "The route for the welcome action on the home controller" should {
    "be /check-your-vat-flat-rate" in {
      controllers.routes.HomeController.welcome().url shouldBe "/check-your-vat-flat-rate"
    }

    "The route for the page one action on the home controller" should {
      " be /check-your-vat-flat-rate/page-one" in {
        controllers.routes.HomeController.pageOne().url shouldBe s"$baseUrl/page-1/"
      }
    }

    "The route for the page two action on the home controller" should {
      "be /check-your-vat-flat-rate/page-two" in {
        controllers.routes.HomeController.pageTwo().url shouldBe s"$baseUrl/page-2/"
      }
    }
  }

}
