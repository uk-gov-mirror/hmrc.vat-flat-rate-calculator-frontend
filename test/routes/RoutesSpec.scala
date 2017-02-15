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

  val vatReturnPeriod: String = baseUrl+"/vat-return-period"

  "The route for the vatReturnPeriod action on the vatReturnPeriod controller" should {
    "be /check-your-vat-flat-rate/vat-return-period" in {
      controllers.routes.VatReturnPeriodController.vatReturnPeriod().url shouldBe s"$vatReturnPeriod"
    }

    "The route for the submitVatReturnPeriod action on the vatReturnPeriod controller" should {
      " be /check-your-vat-flat-rate/page-one" in {
        controllers.routes.VatReturnPeriodController.submitVatReturnPeriod().url shouldBe s"$vatReturnPeriod"
      }
    }
  }

}
