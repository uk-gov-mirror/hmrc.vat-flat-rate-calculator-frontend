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

import common.ResultCodes
import helpers.ControllerTestSpec
import models.VatFlatRateModel
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.StateService
import uk.gov.hmrc.play.http.SessionKeys

import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.Future

class CostOfGoodsControllerSpec extends ControllerTestSpec with ScalaFutures {

  def createTestController(data: Option[VatFlatRateModel]) = {

    def createMockStateService(): StateService = {
      val mockStateService = mock[StateService]

      when(mockStateService.fetchVatFlatRate()(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(Future.successful(data))

      when(mockStateService.saveVatFlatRate(ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(Future.successful(null))

      when(mockStateService.saveResultModel(ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(Future.successful(null))

      mockStateService
    }

    object TestController extends CostOfGoodsController(mockConfig, messages, createMockStateService(), mockValidatedSession, mockForm)
    TestController
  }

  val mockVatReturnPeriodModel = Some(VatFlatRateModel("Annually", Some(999.99), None))
  val mockNoVatReturnPeriodModel = None
  val mockAnnually1000NoneModel = Some(VatFlatRateModel("Annually", Some(1000.00), None))
  val mockQuarterly1000NoneModel = Some(VatFlatRateModel("Quarterly", Some(1000.00), None))

  val mockAnnuallyLessThan1000Model = Some(VatFlatRateModel("Annually", Some(50000.00), Some(500.00)))
  val mockAnnuallyLessThan2PercentModel = Some(VatFlatRateModel("Annually", Some(500000.00), Some(1001.00)))
  val mockAnnuallyBaseModel = Some(VatFlatRateModel("Annually", Some(50049.00), Some(1001.00)))

  val mockQuarterlyLessThan250Model = Some(VatFlatRateModel("Quarterly", Some(10000.00), Some(125.00)))
  val mockQuarterlyLessThan2PercentModel = Some(VatFlatRateModel("Quarterly", Some(100000.00), Some(250.00)))
  val mockQuarterlyBaseModel = Some(VatFlatRateModel("Quarterly", Some(12249.00), Some(251.00)))

  
  "Calling the .costOfGoods action" when {

    "there is no session ID" should {
      val data = mockVatReturnPeriodModel
      lazy val request = FakeRequest("GET", "/")

      lazy val controller = createTestController(data)
      lazy val result = controller.costOfGoods(request)

      "return 303" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "navigate to the timeout page" in {
        redirectLocation(result) shouldBe Some(s"${routes.TimeoutController.timeout()}")
      }
    }

    "there is no model in keystore" should {
      val data = mockNoVatReturnPeriodModel
      lazy val request = FakeRequest("GET", "/").withSession(SessionKeys.sessionId -> s"any-old-id")

      lazy val controller = createTestController(data)
      lazy val result = controller.costOfGoods(request)

      "return 303" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "redirect to the landing page" in {
        redirectLocation(result) shouldBe Some(s"${routes.VatReturnPeriodController.vatReturnPeriod()}")
      }
    }

    "navigating to the costOfGoods page for an annual vat return period" should {
      val data = mockAnnually1000NoneModel
      lazy val request = FakeRequest("GET", "/").withSession(SessionKeys.sessionId -> s"any-old-id")

      lazy val controller = createTestController(data)
      lazy val result = controller.costOfGoods(request)

      "return 200" in {
        status(result) shouldBe Status.OK
      }

      "navigate to the quarterly turnover page" in {
        messages(s"${Jsoup.parse(bodyOf(result)).body.select("h1")}") shouldBe "<h1>"+messages("costOfGoods.heading", messages("common.year"))+"</h1>"
      }
    }

    "navigating to the costOfGoods page for a quarterly vat return period" should {
      val data = mockQuarterly1000NoneModel
      lazy val request = FakeRequest("GET", "/").withSession(SessionKeys.sessionId -> s"any-old-id")

      lazy val controller = createTestController(data)
      lazy val result = controller.costOfGoods(request)

      "return 200" in {
        status(result) shouldBe Status.OK
      }

      "navigate to the quarterly turnover page" in {
        messages(s"${Jsoup.parse(bodyOf(result)).body.select("h1")}") shouldBe "<h1>"+messages("costOfGoods.heading", messages("common.quarter"))+"</h1>"
      }
    }
  }

  "Calling the .submitCostOfGoods action" when {

    "submitting with a correct model for annual, cost<=1000, cost>0.02t" should {
      val data = mockAnnuallyLessThan1000Model
      lazy val request = FakeRequest("POST", "/").withSession(SessionKeys.sessionId -> s"any-old-id").withFormUrlEncodedBody(("vatReturnPeriod", "Annually"),
        ("turnover", "50000.00"),
        ("costOfGoods", "500.00"))

      lazy val controller = createTestController(data)
      lazy val result = controller.submitCostOfGoods(request)

      "return 303" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "return result code 1" in {
        whenReady(controller.whichResult(data.get)) {result => assert(result == ResultCodes.ONE)}
      }
    }

    "submitting with a correct model for annual, cost>=1000, cost<0.02t" should {
      val data = mockAnnuallyLessThan2PercentModel
      lazy val request = FakeRequest("POST", "/").withSession(SessionKeys.sessionId -> s"any-old-id").withFormUrlEncodedBody(("vatReturnPeriod", "Annually"),
        ("turnover", "500000.00"),
        ("costOfGoods", "1001.00"))

      lazy val controller = createTestController(data)
      lazy val result = controller.submitCostOfGoods(request)

      "return 303" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "return result code 2" in {
        whenReady(controller.whichResult(data.get)) {result => assert(result == ResultCodes.TWO)}
      }
    }

    "submitting with a correct model for annual, cost>1000, cost>0.02t" should {
      val data = mockAnnuallyBaseModel
      lazy val request = FakeRequest("POST", "/").withSession(SessionKeys.sessionId -> s"any-old-id").withFormUrlEncodedBody(("vatReturnPeriod", "Annually"),
        ("turnover", "50049.00"),
        ("costOfGoods", "1001.00"))

      lazy val controller = createTestController(data)
      lazy val result = controller.submitCostOfGoods(request)

      "return 303" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "return result code 3" in {
        whenReady(controller.whichResult(data.get)) {result => assert(result == ResultCodes.THREE)}
      }
    }

    "submitting with a correct model for quarterly, cost<=250, cost>0.02t" should {
      val data = mockQuarterlyLessThan250Model
      lazy val request = FakeRequest("POST", "/").withSession(SessionKeys.sessionId -> s"any-old-id").withFormUrlEncodedBody(("vatReturnPeriod", "Quarterly"),
        ("turnover", "10000.00"),
        ("costOfGoods", "125.00"))

      lazy val controller = createTestController(data)
      lazy val result = controller.submitCostOfGoods(request)

      "return 303" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "return result code 4" in {
        whenReady(controller.whichResult(data.get)) {result => assert(result == ResultCodes.FOUR)}
      }
    }

    "submitting with a correct model for quarterly, cost>250, cost<0.02t" should {
      val data = mockQuarterlyLessThan2PercentModel
      lazy val request = FakeRequest("POST", "/").withSession(SessionKeys.sessionId -> s"any-old-id").withFormUrlEncodedBody(("vatReturnPeriod", "Quarterly"),
        ("turnover", "100000.00"),
        ("costOfGoods", "250.00"))

      lazy val controller = createTestController(data)
      lazy val result = controller.submitCostOfGoods(request)

      "return 303" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "return result code 5" in {
        whenReady(controller.whichResult(data.get)) {result => assert(result == ResultCodes.FIVE)}
      }
    }

    "submitting with a correct model for quarterly, cost>250, cost>0.02t" should {
      val data = mockQuarterlyBaseModel
      lazy val request = FakeRequest("POST", "/").withSession(SessionKeys.sessionId -> s"any-old-id").withFormUrlEncodedBody(("vatReturnPeriod", "Quarterly"),
        ("turnover", "12549.00"),
        ("costOfGoods", "251.00"))

      lazy val controller = createTestController(data)
      lazy val result = controller.submitCostOfGoods(request)

      "return 303" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "return result code 6" in {
        whenReady(controller.whichResult(data.get)) {result => assert(result == ResultCodes.SIX)}
      }
    }

    "there is an error with the form for an annual model" should {

      val data = mockAnnuallyLessThan1000Model
      lazy val request = FakeRequest("POST", "/").withSession(SessionKeys.sessionId -> s"any-old-id")

      lazy val controller = createTestController(data)
      lazy val result = controller.submitCostOfGoods(request)

      "return 400" in {
        status(result) shouldBe Status.BAD_REQUEST
      }

    }

    "there is an error with the form for a quarterly model" should {

      val data = mockQuarterlyLessThan250Model
      lazy val request = FakeRequest("POST", "/").withSession(SessionKeys.sessionId -> s"any-old-id")

      lazy val controller = createTestController(data)
      lazy val result = controller.submitCostOfGoods(request)

      "return 400" in {
        status(result) shouldBe Status.BAD_REQUEST
      }

    }

    "there is an error with the form and no model" should {

      val data = mockNoVatReturnPeriodModel
      lazy val request = FakeRequest("POST", "/").withSession(SessionKeys.sessionId -> s"any-old-id")

      lazy val controller = createTestController(data)
      lazy val result = controller.submitCostOfGoods(request)

      "return Internal Server Error" in {
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }

    }

  }


}
