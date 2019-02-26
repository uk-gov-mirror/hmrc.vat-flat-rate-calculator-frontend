/*
 * Copyright 2019 HM Revenue & Customs
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
import helpers.ViewSpecHelpers.CostOfGoodsViewMessages
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.UnitSpec
import org.jsoup.Jsoup
import play.api.inject.Injector
import views.html.home.costOfGoods
import play.api.i18n.Messages.Implicits._

class CostOfGoodsViewSpec extends UnitSpec with GuiceOneAppPerSuite with CostOfGoodsViewMessages {

  implicit lazy val fakeRequest = FakeRequest()
  def injector: Injector = app.injector
  def appConfig: AppConfig = injector.instanceOf[AppConfig]
  lazy val mockForm: VatFlatRateForm = injector.instanceOf[VatFlatRateForm]
  val costOfGoodsPeriod = "annually"


  "the CostOfGoodsView" should {
    lazy val CostOfGoodsForm = mockForm.costOfGoodsForm.bind(Map("vatReturnPeriod" -> "annually",
                                                                 "turnover" -> "1000",
                                                                 "costOfGoods" -> "100"))

    lazy val view = costOfGoods(appConfig ,CostOfGoodsForm, costOfGoodsPeriod)
    lazy val doc = Jsoup.parse(view.body)

    lazy val errorCostOfGoodsForm = mockForm.costOfGoodsForm.bind(Map("vatReturnPeriod" -> "annually"))

    lazy val errorView = costOfGoods(appConfig ,errorCostOfGoodsForm, costOfGoodsPeriod)
    lazy val errorDoc = Jsoup.parse(errorView.body)

    "have the correct title" in {
      doc.title() shouldBe costOfGoodTitle
    }

    "have the correct heading" in {
      doc.select("h1").text() shouldBe  costOfGoodsHeading(costOfGoodsPeriod)
    }

    "have some introductory text" in {
      doc.select("div > p").eq(1).text shouldBe costOfGoodsParagraph1
    }

    "have a h2 with text" in {
      doc.select("h2").text() shouldBe costOfGoodDontInclude
    }

    "have a list of bulletpoints" in {
      doc.select("ul > li").eq(1).text() shouldBe costOfGoodsBullet1
      doc.select("ul > li").eq(2).text() shouldBe costOfGoodsBullet2
      doc.select("ul > li").eq(3).text() shouldBe costOfGoodsBullet3
      doc.select("ul > li").eq(4).text() shouldBe costOfGoodsBullet4
      doc.select("ul > li").eq(5).text() shouldBe costOfGoodsBullet5
      doc.select("ul > li").eq(6).text() shouldBe costOfGoodsBullet6
      doc.select("ul > li").eq(7).text() shouldBe costOfGoodsBullet7
      doc.select("ul > li").eq(8).text() shouldBe costOfGoodsBullet8
      doc.select("ul > li").eq(9).text() shouldBe costOfGoodsBullet9
    }

    "have text that contains a link" in {
      doc.select("p").eq(2).text() shouldBe costOfGoodsParagraph2
      doc.select("p").eq(2).attr("href") shouldBe ""
    }

    "have a £ symbol present" in {
      doc.select(".poundSign").text shouldBe "£"
    }

    "display the correct error" in {
      errorCostOfGoodsForm.hasErrors shouldBe true
      errorDoc.select("span.error-notification").eq(0).text shouldBe costOfGoodsError
    }

    "have a continue button" in{
      doc.select("button").text shouldBe costOfGoodsContinue
      doc.select("button").attr("type") shouldBe "submit"
    }

    "have a valid form" in{
      doc.select("form").attr("method") shouldBe "POST"
      doc.select("form").attr("action") shouldBe controllers.routes.CostOfGoodsController.submitCostOfGoods().url
    }

  }

}
