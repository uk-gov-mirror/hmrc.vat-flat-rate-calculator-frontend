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

import config.AppConfig
import controllers.predicates.ValidatedSession
import forms.VatFlatRateForm
import models.VatFlatRateModel
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.OneAppPerSuite
import play.api.http.Status
import play.api.i18n.MessagesApi
import play.api.inject.Injector
import play.api.test.FakeRequest
import services.StateService
import uk.gov.hmrc.play.http.SessionKeys
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future


class VatReturnPeriodControllerSpec extends UnitSpec with OneAppPerSuite with MockitoSugar {

  val injector: Injector = app.injector

  lazy val messages: MessagesApi = injector.instanceOf[MessagesApi]
  lazy val mockConfig: AppConfig = injector.instanceOf[AppConfig]
  lazy val mockValidatedSession: ValidatedSession = injector.instanceOf[ValidatedSession]
  lazy val mockForm: VatFlatRateForm = app.injector.instanceOf[VatFlatRateForm]
  lazy val mockStateService: StateService = createMockStateService(mockVatReturnPeriodModel)

  val mockVatReturnPeriodModel = Some(VatFlatRateModel("annual", None, None))

  def createMockStateService(data: Option[VatFlatRateModel]): StateService = {

    val mockStateService = mock[StateService]

    when(mockStateService.fetchVatFlatRate()(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(data))

    mockStateService
  }

  val controller = new VatReturnPeriodController(mockConfig, messages, mockStateService, mockValidatedSession, mockForm)

    "Navigating to the landing page" when {

    "there is no sessionId" should {
      lazy val request = FakeRequest("GET", "/")
      lazy val result = controller.vatReturnPeriod(request)

      "generate a sessionId and continue" in {
        status(result) shouldBe Status.OK
      }
    }
    "there is a sessionId" should {
      val sessionId =  UUID.randomUUID().toString
      lazy val request = FakeRequest("GET", "/").withSession(SessionKeys.sessionId -> s"sessionId-$sessionId")
      lazy val result = controller.vatReturnPeriod(request)

      "return 200 " in {
        status(result) shouldBe Status.OK
      }
    }
  }
}
