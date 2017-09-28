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

import helpers.ControllerTestSpec
import models.{ResultModel, VatFlatRateModel}
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when
import play.api.http.Status
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.test.FakeRequest
import services.StateService

import scala.concurrent.Future
import uk.gov.hmrc.http.SessionKeys


class ResultControllerSpec extends ControllerTestSpec {

  def createTestController(data: Option[ResultModel]): ResultController = {
    object TestResultController extends ResultController(mockConfig, messages, createMockStateService(), mockValidatedSession)
    def createMockStateService(): StateService = {
      val mockStateService = mock[StateService]

      when(mockStateService.fetchResultModel()(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(Future.successful(data))

      mockStateService
    }
    TestResultController
  }



  "Navigating to the result page without a model in keystore" should {
    val data = None
    lazy val controller = createTestController(data)
    lazy val request = FakeRequest()
      .withSession(SessionKeys.sessionId -> s"any-old-id")

    lazy val result = controller.result(request)

    "return 500" in {
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
    }

    "navigate to the technical error page" in {
      Jsoup.parse(bodyOf(result)).title shouldBe Messages("techError.title")
    }
  }

  "Navigating to the result page with a model in keystore, resultCode 1" should {
    val data = Some(ResultModel(VatFlatRateModel("annually", Some(2000), Some(500)), 1))
    lazy val request = FakeRequest()
      .withSession(SessionKeys.sessionId -> s"any-old-id")
    lazy val controller = createTestController(data)
    lazy val result = controller.result(request)

    "return 200" in {
      status(result) shouldBe Status.OK
    }

    "navigate to the result page" in {
      Jsoup.parse(bodyOf(result)).title shouldBe Messages("result.title")
    }
  }

  "Navigating to the result page with a model in keystore, resultCode 2" should {
    val data = Some(ResultModel(VatFlatRateModel("annually", Some(50001), Some(1000)), 2))
    lazy val request = FakeRequest()
      .withSession(SessionKeys.sessionId -> s"any-old-id")
    lazy val controller = createTestController(data)
    lazy val result = controller.result(request)

    "return 200" in {
      status(result) shouldBe Status.OK
    }

    "navigate to the result page" in {
      Jsoup.parse(bodyOf(result)).title shouldBe Messages("result.title")
    }
  }

  "Navigating to the result page with a model in keystore, resultCode 3" should {
    val data = Some(ResultModel(VatFlatRateModel("annually", Some(5000), Some(1000)), 3))
    lazy val request = FakeRequest()
      .withSession(SessionKeys.sessionId -> s"any-old-id")
    lazy val controller = createTestController(data)
    lazy val result = controller.result(request)

    "return 200" in {
      status(result) shouldBe Status.OK
    }

    "navigate to the result page" in {
      Jsoup.parse(bodyOf(result)).title shouldBe Messages("result.title")
    }
  }

  "Navigating to the result page with a model in keystore, resultCode 4" should {
    val data = Some(ResultModel(VatFlatRateModel("quarterly", Some(2000), Some(100)), 4))
    lazy val request = FakeRequest()
      .withSession(SessionKeys.sessionId -> s"any-old-id")
    lazy val controller = createTestController(data)
    lazy val result = controller.result(request)

    "return 200" in {
      status(result) shouldBe Status.OK
    }

    "navigate to the result page" in {
      Jsoup.parse(bodyOf(result)).title shouldBe Messages("result.title")
    }
  }

  "Navigating to the result page with a model in keystore, resultCode 5" should {
    val data = Some(ResultModel(VatFlatRateModel("quarterly", Some(12501), Some(250)), 5))
    lazy val request = FakeRequest()
      .withSession(SessionKeys.sessionId -> s"any-old-id")
    lazy val controller = createTestController(data)
    lazy val result = controller.result(request)

    "return 200" in {
      status(result) shouldBe Status.OK
    }

    "navigate to the result page" in {
      Jsoup.parse(bodyOf(result)).title shouldBe Messages("result.title")
    }
  }

  "Navigating to the result page with a model in keystore, resultCode 6" should {
    val data = Some(ResultModel(VatFlatRateModel("quarterly", Some(12500), Some(250)), 6))
    lazy val request = FakeRequest()
      .withSession(SessionKeys.sessionId -> s"any-old-id")
    lazy val controller = createTestController(data)
    lazy val result = controller.result(request)

    "return 200" in {
      status(result) shouldBe Status.OK
    }

    "navigate to the result page" in {
      Jsoup.parse(bodyOf(result)).title shouldBe Messages("result.title")
    }
  }

}
