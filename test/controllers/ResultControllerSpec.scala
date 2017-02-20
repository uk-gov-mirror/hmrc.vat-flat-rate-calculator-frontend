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
import uk.gov.hmrc.play.http.SessionKeys

import scala.concurrent.Future


class ResultControllerSpec extends ControllerTestSpec {

  def createTestMethod(data: Option[ResultModel]) = {
    object TestResultController extends ResultController(mockConfig, messages, createMockStateService(data:Option[ResultModel]), mockValidatedSession)
    TestResultController
  }
  val data = None
  object TestResultController extends ResultController(mockConfig, messages, createMockStateService(data:Option[ResultModel]), mockValidatedSession)

  def createMockStateService(data: Option[ResultModel]): StateService = {

    val mockStateService = mock[StateService]

    when(mockStateService.fetchResultModel()(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(data))

    mockStateService
  }

  "Navigating to the result page without a model in keystore" should {

    lazy val request = FakeRequest()
      .withSession(SessionKeys.sessionId -> s"any-old-id")

    lazy val result = TestResultController.result(request)

    "return 500" in {
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
    }

    "navigate to the technical error page" in {
      Jsoup.parse(bodyOf(result)).title shouldBe Messages("techError.title")
    }
  }

//  "Navigating to the result page with a model in keystore" should {
//    val data = Some(ResultModel(VatFlatRateModel("annually", Some(2000), Some(500)), 1))
//    lazy val request = FakeRequest()
//      .withSession(SessionKeys.sessionId -> s"any-old-id")
//    lazy val mockStateService = createMockStateService(data)
//    val controller = new ResultController(mockConfig, messages, mockStateService, mockValidatedSession)
//    lazy val result = controller.result(request)
//
//    "return 500" in {
//      status(result) shouldBe Status.OK
//    }
//
//    "navigate to the technical error page" in {
//      Jsoup.parse(bodyOf(result)).title shouldBe Messages("result.title")
//    }
//  }

}
