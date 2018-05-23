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
import helpers.ViewSpecHelpers.VatReturnPeriodViewMessages
import org.jsoup.Jsoup
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.Messages.Implicits._
import play.api.inject.Injector
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.UnitSpec
import views.html.home.vatReturnPeriod

class vatReturnPeriodViewSpec extends UnitSpec with GuiceOneAppPerSuite with VatReturnPeriodViewMessages {

  implicit lazy val fakeRequest = FakeRequest()
  def injector: Injector = app.injector
  def appConfig: AppConfig = injector.instanceOf[AppConfig]
  lazy val mockForm: VatFlatRateForm = injector.instanceOf[VatFlatRateForm]
  val Period = "annually"


  "the VatReturnPeriod" should {
    lazy val VatReturnPeriodForm = mockForm.vatReturnPeriodForm.bind(Map("vatReturnPeriod" -> "annually",
      "turnover" -> "1000",
      "costOfGoods" -> "100"))
    lazy val view = vatReturnPeriod(appConfig ,VatReturnPeriodForm)
    lazy val doc = Jsoup.parse(view.body)

    lazy val errorVatReturnPeriodForm = mockForm.vatReturnPeriodForm.bind(Map("" -> ""))
    lazy val errorView = vatReturnPeriod(appConfig ,errorVatReturnPeriodForm)
    lazy val errorDoc = Jsoup.parse(errorView.body)


    "have the correct title" in {
      doc.title() shouldBe vatReturnPeriodTitle
    }

    "have the correct heading" in {
      doc.select("h1").text() shouldBe  vatReturnPeriodHeading
    }

    "have some introductory text" in {
      doc.select("div > p").eq(1).text shouldBe vatReturnPeriodIntro
    }

    "have a paragraph text" in {
      doc.select("div > p").eq(2).text shouldBe vatReturnPeriodPara
    }

    "have a 'annually' label on radio button" in {
      doc.select("label").first().text shouldBe vatReturnPeriodAnnually
    }

    "have a 'quarterly' label on radio button" in {
      doc.select("label").eq(1).text shouldBe vatReturnPeriodQuarterly
    }

    "display the correct error" in {
      errorVatReturnPeriodForm.hasErrors shouldBe true
      errorDoc.select("span.error-notification").eq(0).text shouldBe vatReturnPeriodError
    }

    "have a continue button" in{
      doc.select("button").text shouldBe vatReturnPeriodContinue
      doc.select("button").attr("type") shouldBe "submit"
    }

    "have a valid form" in{
      doc.select("form").attr("method") shouldBe "POST"
      doc.select("form").attr("action") shouldBe controllers.routes.VatReturnPeriodController.submitVatReturnPeriod().url
    }
  }

}
