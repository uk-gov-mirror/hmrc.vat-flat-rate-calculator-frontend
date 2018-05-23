/*
 * Copyright 2018 HM Revenue & Customs
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

package views.home

import config.AppConfig
import forms.VatFlatRateForm
import helpers.ViewSpecHelpers.TurnoverViewMessages
import org.jsoup.Jsoup
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.Messages.Implicits._
import play.api.inject.Injector
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.UnitSpec
import views.html.home.turnover

class turnoverViewSpec extends UnitSpec with GuiceOneAppPerSuite with TurnoverViewMessages {

  implicit lazy val fakeRequest = FakeRequest()
  def injector: Injector = app.injector
  def appConfig: AppConfig = injector.instanceOf[AppConfig]
  lazy val mockForm: VatFlatRateForm = injector.instanceOf[VatFlatRateForm]
  val turnoverPeriodString = "year"

  "the TurnoverView" should {
    lazy val TurnoverForm = mockForm.turnoverForm.bind(Map("vatReturnPeriod" -> "annually",
      "turnover" -> "1000",
      "costOfGoods" -> "100"))
    lazy val view = turnover(appConfig ,TurnoverForm, turnoverPeriodString)
    lazy val doc = Jsoup.parse(view.body)

    lazy val errorTurnoverForm = mockForm.turnoverForm.bind(Map("vatReturnPeriod" -> "annually"))
    lazy val errorView = turnover(appConfig ,errorTurnoverForm, turnoverPeriodString)
    lazy val errorDoc = Jsoup.parse(errorView.body)

    "have the correct title" in {
      doc.title() shouldBe turnoverTitle
    }

    "have the correct heading" in {
      doc.select("h1").text() shouldBe  turnoverHeading(turnoverPeriodString)
    }

    "have some introductory text" in {
      doc.select("div > p").eq(1).text shouldBe turnoverIntro
    }

    "have a £ symbol present" in {
      doc.select(".poundSign").text shouldBe "£"
    }

    "display the correct error" in {
      errorTurnoverForm.hasErrors shouldBe true
      errorDoc.select("span.error-notification").eq(0).text shouldBe turnoverError
    }

    "have a continue button" in{
      doc.select("button").text shouldBe turnoverContinue
      doc.select("button").attr("type") shouldBe "submit"
    }

    "have a valid form" in{
      doc.select("form").attr("method") shouldBe "POST"
      doc.select("form").attr("action") shouldBe controllers.routes.TurnoverController.submitTurnover.url
    }
  }

}
