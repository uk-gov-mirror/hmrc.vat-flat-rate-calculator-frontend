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
import helpers.ViewSpecHelpers.ResultViewMessages
import org.jsoup.Jsoup
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.Messages.Implicits._
import play.api.inject.Injector
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.UnitSpec
import views.html.home.result

class resultViewSpec extends UnitSpec with GuiceOneAppPerSuite with ResultViewMessages {

  implicit lazy val fakeRequest = FakeRequest()
  def injector: Injector = app.injector
  def appConfig: AppConfig = injector.instanceOf[AppConfig]
  val resultCode = 1
  val showUserResearchPanel = true
  val gaData = Array("annually", "1000", "100")


  "the ResultView" should {

    lazy val view = result(appConfig ,resultCode, gaData, showUserResearchPanel)
    lazy val doc = Jsoup.parse(view.body)

    "have the correct title" in {
      doc.title() shouldBe ResultTitle
    }

    "have the correct heading" in {
      doc.select("span#resultOutcome").text() shouldBe  ResultHeading
    }

    "have some introductory text" in {
      doc.select("div > p").eq(1).text() shouldBe ResultIntro
    }

    "have a header for progressive disclosure" in {
      doc.select("strong").text() shouldBe ResultProgressiveDisclosureHeader
    }

    "have a progressive disclosure" in {
      doc.select("span.summary").text() shouldBe ResultProgressiveDisclosureTitle
    }

    "have text within progressive disclosure" in {
      doc.select("div.panel-indent > p").text() shouldBe ResultProgressiveDisclosureText
    }

    "have a h2 with text" in {
      doc.select("h2").text() shouldBe ResultH2Text
    }

    "have instructions on what happens next" in {
      doc.select("div.form-group > p").eq(1).text() shouldBe ResultNextText1

      doc.select("div.form-group > p").eq(2).text() shouldBe ResultNextText2
      doc.select("div.form-group > p > a").eq(0).attr("href") shouldBe ResultNextText2Href

      doc.select("div.form-group > p").eq(3).text() shouldBe ResultNextText3
      doc.select("div.form-group > p > a").eq(1).attr("href") shouldBe ResultNextText3Href

      doc.select("div.form-group > p").eq(4).text() shouldBe ResultNextText4
      doc.select("div.form-group > p > a").eq(2).attr("href") shouldBe ResultNextText4Href
    }

    "have a user research banner" in {
      doc.select("div.banner-panel__title").text() shouldBe ResultBannerTitle
      doc.select("a").eq(4).text() shouldBe ResultBannerText
      doc.select("a").eq(4).attr("href") shouldBe ResultBannerTextHref
      doc.select("a > span").eq(0).text() shouldBe ResultBannerClose
    }

  }

}
