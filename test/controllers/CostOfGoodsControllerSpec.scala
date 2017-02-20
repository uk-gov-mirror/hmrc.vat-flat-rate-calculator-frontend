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

package controllers

import akka.stream.Materializer
import config.AppConfig
import controllers.predicates.ValidatedSession
import forms.VatFlatRateForm
import models.VatFlatRateModel
import org.scalatest.mock.MockitoSugar
import play.api.http.Status
import play.api.i18n.MessagesApi
import play.api.inject.Injector
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.StateService
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.http.SessionKeys
import uk.gov.hmrc.play.test.UnitSpec
import org.scalatestplus.play.OneAppPerSuite
import org.jsoup.Jsoup

import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when

import scala.concurrent.Future

/**
 * Created by sam on 17/02/17.
 */

class CostOfGoodsControllerSpec extends UnitSpec with MockitoSugar with OneAppPerSuite{

  val injector: Injector = app.injector
  implicit val mat: Materializer = app.injector.instanceOf[Materializer]

  lazy val messages: MessagesApi = injector.instanceOf[MessagesApi]
  lazy val mockConfig: AppConfig = injector.instanceOf[AppConfig]
  lazy val mockValidatedSession: ValidatedSession = injector.instanceOf[ValidatedSession]
  lazy val mockForm: VatFlatRateForm = app.injector.instanceOf[VatFlatRateForm]

  val mockVatReturnPeriodModel = Some(VatFlatRateModel("Annually", Some(999.99), None))
  val mockNoVatReturnPeriodModel = None
  val mockAnnually1000NoneModel = Some(VatFlatRateModel("Annually", Some(1000.00), None))
  val mockQuarterly1000NoneModel = Some(VatFlatRateModel("Quarterly", Some(1000.00), None))

  val mockAnnuallyLessThan1000Model = Some(VatFlatRateModel("Annually", Some(50000.00), Some(500.00)))
  val mockAnnuallyLessThan2PercentModel = Some(VatFlatRateModel("Annually", Some(50050.00), Some(1001.00)))
  val mockAnnuallyBaseModel = Some(VatFlatRateModel("Annually", Some(50049.00), Some(1001.00)))


  val mockQuarterlyLessThan250Model = Some(VatFlatRateModel("Quarterly", Some(10000.00), Some(125.00)))
  val mockQuarterlyLessThan2PercentModel = Some(VatFlatRateModel("Quarterly", Some(12550.00), Some(251.00)))
  val mockQuarterlyBaseModel = Some(VatFlatRateModel("Quarterly", Some(12249.00), Some(251.00)))



  def createMockStateService(data: Option[VatFlatRateModel]): StateService = {

    val mockStateService = mock[StateService]

    when(mockStateService.fetchVatFlatRate()(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(data))

    mockStateService
  }

  def createMockStateServiceSubmit(data: Option[VatFlatRateModel]) = {
    val mockStateService = mock[StateService]

    when(mockStateService.fetchVatFlatRate()(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(data))

    when(mockStateService.saveVatFlatRate(ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(null))

    when(mockStateService.saveResultModel(ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(null))

    mockStateService
  }


  "Calling the .costOfGoods action" when {

    "there is no session ID" should {
      lazy val mockStateService = createMockStateService(mockVatReturnPeriodModel)
      lazy val request = FakeRequest("GET", "/")

      val controller = new CostOfGoodsController(mockConfig, messages, mockStateService, mockValidatedSession, mockForm)
      lazy val result = controller.costOfGoods(request)

      "return 303" in {
        status(result) shouldBe 303
      }

      "navigate to the landing page" in {
        redirectLocation(result) shouldBe Some(s"${routes.VatReturnPeriodController.vatReturnPeriod()}")
      }
    }

    "there is no model in keystore" should {
      lazy val mockStateService = createMockStateService(mockNoVatReturnPeriodModel)
      lazy val request = FakeRequest("GET", "/").withSession(SessionKeys.sessionId -> s"any-old-id")

      val controller = new CostOfGoodsController(mockConfig, messages, mockStateService, mockValidatedSession, mockForm)
      lazy val result = controller.costOfGoods(request)

      "return 303" in {
        status(result) shouldBe 303
      }

      "redirect to the landing page" in {
        redirectLocation(result) shouldBe Some(s"${routes.VatReturnPeriodController.vatReturnPeriod()}")
      }
    }

    "navigating to the costOfGoods page for an annual vat return period" should {
      lazy val mockStateService = createMockStateService(mockAnnually1000NoneModel)
      lazy val request = FakeRequest("GET", "/").withSession(SessionKeys.sessionId -> s"any-old-id")

      val controller = new CostOfGoodsController(mockConfig, messages, mockStateService, mockValidatedSession, mockForm)
      lazy val result = controller.costOfGoods(request)

      "return 200" in {
        status(result) shouldBe 200
      }

      "navigate to the quarterly turnover page" in {
        messages(s"${Jsoup.parse(bodyOf(result)).body.select("h1")}") shouldBe "<h1>"+messages("costOfGoods.heading", messages("common.year"))+"</h1>"
      }
    }

    "navigating to the costOfGoods page for a quarterly vat return period" should {
      lazy val mockStateService = createMockStateService(mockQuarterly1000NoneModel)
      lazy val request = FakeRequest("GET", "/").withSession(SessionKeys.sessionId -> s"any-old-id")

      val controller = new CostOfGoodsController(mockConfig, messages, mockStateService, mockValidatedSession, mockForm)
      lazy val result = controller.costOfGoods(request)

      "return 200" in {
        status(result) shouldBe 200
      }

      "navigate to the quarterly turnover page" in {
        messages(s"${Jsoup.parse(bodyOf(result)).body.select("h1")}") shouldBe "<h1>"+messages("costOfGoods.heading", messages("common.quarter"))+"</h1>"
      }
    }
  }

  "Calling the .submitCostOfGoods action" when {

    "submitting with a correct model for annual, cost<=1000, cost>0.02t" should {
      lazy val mockStateService = createMockStateServiceSubmit(mockAnnuallyLessThan1000Model)
      lazy val request = FakeRequest("POST", "/").withSession(SessionKeys.sessionId -> s"any-old-id").withFormUrlEncodedBody(("vatReturnPeriod", "Annually"),
        ("turnover", "50000.00"),
        ("costOfGoods", "500.00"))

      val controller = new CostOfGoodsController(mockConfig, messages, mockStateService, mockValidatedSession, mockForm)
      lazy val result = controller.submitCostOfGoods(request)

      "return 303" in {
        status(result) shouldBe 303
      }
    }

    "submitting with a correct model for annual, cost>1000, cost<=0.02t" should {
      lazy val mockStateService = createMockStateServiceSubmit(mockAnnuallyLessThan2PercentModel)
      lazy val request = FakeRequest("POST", "/").withSession(SessionKeys.sessionId -> s"any-old-id").withFormUrlEncodedBody(("vatReturnPeriod", "Annually"),
        ("turnover", "50050.00"),
        ("costOfGoods", "1001.00"))

      val controller = new CostOfGoodsController(mockConfig, messages, mockStateService, mockValidatedSession, mockForm)
      lazy val result = controller.submitCostOfGoods(request)

      "return 303" in {
        status(result) shouldBe 303
      }
    }

    "submitting with a correct model for annual, cost>1000, cost>0.02t" should {
      lazy val mockStateService = createMockStateServiceSubmit(mockAnnuallyBaseModel)
      lazy val request = FakeRequest("POST", "/").withSession(SessionKeys.sessionId -> s"any-old-id").withFormUrlEncodedBody(("vatReturnPeriod", "Annually"),
        ("turnover", "50049.00"),
        ("costOfGoods", "1001.00"))

      val controller = new CostOfGoodsController(mockConfig, messages, mockStateService, mockValidatedSession, mockForm)
      lazy val result = controller.submitCostOfGoods(request)

      "return 303" in {
        status(result) shouldBe 303
      }
    }

    "submitting with a correct model for quarterly, cost<=250, cost>0.02t" should {
      lazy val mockStateService = createMockStateServiceSubmit(mockQuarterlyLessThan250Model)
      lazy val request = FakeRequest("POST", "/").withSession(SessionKeys.sessionId -> s"any-old-id").withFormUrlEncodedBody(("vatReturnPeriod", "Quarterly"),
        ("turnover", "10000.00"),
        ("costOfGoods", "125.00"))

      val controller = new CostOfGoodsController(mockConfig, messages, mockStateService, mockValidatedSession, mockForm)
      lazy val result = controller.submitCostOfGoods(request)

      "return 303" in {
        status(result) shouldBe 303
      }
    }

    "submitting with a correct model for quarterly, cost>250, cost<=0.02t" should {
      lazy val mockStateService = createMockStateServiceSubmit(mockQuarterlyLessThan2PercentModel)
      lazy val request = FakeRequest("POST", "/").withSession(SessionKeys.sessionId -> s"any-old-id").withFormUrlEncodedBody(("vatReturnPeriod", "Quarterly"),
        ("turnover", "12550.00"),
        ("costOfGoods", "251.00"))

      val controller = new CostOfGoodsController(mockConfig, messages, mockStateService, mockValidatedSession, mockForm)
      lazy val result = controller.submitCostOfGoods(request)

      "return 303" in {
        status(result) shouldBe 303
      }
    }

    "submitting with a correct model for quarterly, cost>250, cost>0.02t" should {
      lazy val mockStateService = createMockStateServiceSubmit(mockQuarterlyBaseModel)
      lazy val request = FakeRequest("POST", "/").withSession(SessionKeys.sessionId -> s"any-old-id").withFormUrlEncodedBody(("vatReturnPeriod", "Quarterly"),
        ("turnover", "12549.00"),
        ("costOfGoods", "251.00"))

      val controller = new CostOfGoodsController(mockConfig, messages, mockStateService, mockValidatedSession, mockForm)
      lazy val result = controller.submitCostOfGoods(request)

      "return 303" in {
        status(result) shouldBe 303
      }
    }

    "there is an error with the form for an annual model" should {

      lazy val mockStateService = createMockStateServiceSubmit(mockAnnuallyLessThan1000Model)
      lazy val request = FakeRequest("POST", "/").withSession(SessionKeys.sessionId -> s"any-old-id")

      val controller = new CostOfGoodsController(mockConfig, messages, mockStateService, mockValidatedSession, mockForm)
      lazy val result = controller.submitCostOfGoods(request)

      "return 400" in {
        status(result) shouldBe 400
      }

    }

    "there is an error with the form for a quarterly model" should {

      lazy val mockStateService = createMockStateServiceSubmit(mockQuarterlyLessThan250Model)
      lazy val request = FakeRequest("POST", "/").withSession(SessionKeys.sessionId -> s"any-old-id")

      val controller = new CostOfGoodsController(mockConfig, messages, mockStateService, mockValidatedSession, mockForm)
      lazy val result = controller.submitCostOfGoods(request)

      "return 400" in {
        status(result) shouldBe 400
      }

    }

    "there is an error with the form and no model" should {

      lazy val mockStateService = createMockStateServiceSubmit(mockNoVatReturnPeriodModel)
      lazy val request = FakeRequest("POST", "/").withSession(SessionKeys.sessionId -> s"any-old-id")

      val controller = new CostOfGoodsController(mockConfig, messages, mockStateService, mockValidatedSession, mockForm)
      lazy val result = controller.submitCostOfGoods(request)

      "return Internal Server Error" in {
        status(result) shouldBe 500
      }

    }

  }


}
