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

import java.util.UUID

import forms.VatFlatRateForm
import helpers.ControllerTestSpec
import models.VatFlatRateModel
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import play.api.http.Status
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.StateService
import uk.gov.hmrc.play.http.SessionKeys

import scala.concurrent.Future


class VatReturnPeriodControllerSpec extends ControllerTestSpec {

  def createMockStateService(data: Option[VatFlatRateModel]): StateService = {

    val mockStateService = mock[StateService]

    when(mockStateService.fetchVatFlatRate()(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(data))

    mockStateService
  }

  "Navigating to the landing page" when {
    val sessionId =  UUID.randomUUID().toString

    "there is no sessionId" should {

      lazy val mockStateService = createMockStateService(None)
      lazy val request = FakeRequest("GET", "/")
      val controller = new VatReturnPeriodController(mockConfig, messages, mockStateService, mockValidatedSession, mockForm)
      lazy val result = controller.vatReturnPeriod(request)

      "generate a sessionId and continue" in {
        status(result) shouldBe Status.OK
      }
    }
    "there is no previous model in keystore" should {
      lazy val data = None
      lazy val mockStateService = createMockStateService(data)
      lazy val request = FakeRequest("GET", "/").withSession(SessionKeys.sessionId -> s"sessionId-$sessionId")
      val controller = new VatReturnPeriodController(mockConfig, messages, mockStateService, mockValidatedSession, mockForm)
      lazy val result = controller.vatReturnPeriod(request)

      "return 200 " in {
        status(result) shouldBe Status.OK
      }

      "navigate to the turnover page" in {
        Jsoup.parse(bodyOf(result)).title shouldBe Messages("vatReturnPeriod.title")
      }
    }

    "there is a model in keystore" should {
      lazy val data = Some(VatFlatRateModel("annual", None, None))
      lazy val mockStateService = createMockStateService(data)
      lazy val request = FakeRequest("GET", "/").withSession(SessionKeys.sessionId -> s"sessionId-$sessionId")
      val controller = new VatReturnPeriodController(mockConfig, messages, mockStateService, mockValidatedSession, mockForm)
      lazy val result = controller.vatReturnPeriod(request)

      "return 200 " in {
        status(result) shouldBe Status.OK
      }

      "navigate to the turnover page" in {
        Jsoup.parse(bodyOf(result)).title shouldBe Messages("vatReturnPeriod.title")
      }
    }
  }

  "Calling the .submitVatReturnPeriod action" when {

    "not entering any data" should {
      val data = None
      lazy val request = FakeRequest()
        .withSession(SessionKeys.sessionId -> s"any-old-id")
      lazy val mockStateService = createMockStateService(data)
      val controller = new VatReturnPeriodController(mockConfig, messages, mockStateService, mockValidatedSession, mockForm)
      lazy val result = controller.submitVatReturnPeriod(request)

      "return 400" in {
        status(result) shouldBe Status.BAD_REQUEST
      }
      "fail with the correct error message" in {
        Jsoup.parse(bodyOf(result)).getElementsByClass("error-notification").text should include(Messages("error.required"))
      }
    }

    "submitting a valid Vat Return Period" should {
      val data = None
      lazy val request = FakeRequest()
        .withSession(SessionKeys.sessionId -> s"any-old-id")
        .withFormUrlEncodedBody(("vatReturnPeriod","annually"))
      lazy val mockStateService = createMockStateService(data)
      val controller = new VatReturnPeriodController(mockConfig, messages, mockStateService, mockValidatedSession, mockForm)
      lazy val result = controller.submitVatReturnPeriod(request)


      "return 303" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "redirect to the cost of goods page" in {
        redirectLocation(result) shouldBe Some(s"${controllers.routes.TurnoverController.turnover()}")
      }
    }

  }

}