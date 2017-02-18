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

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import assets.MessageLookup
import config.AppConfig
import controllers.predicates.ValidatedSession
import forms.VatFlatRateForm
import models.VatFlatRateModel
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.OneAppPerSuite
import play.api.http.Status
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.Injector
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import services.StateService
import uk.gov.hmrc.play.http.SessionKeys
import uk.gov.hmrc.play.test.UnitSpec
import play.api.test.Helpers._
import views.html.home.result

import scala.concurrent.Future

class TurnoverControllerSpec extends UnitSpec with MockitoSugar with ScalaFutures with OneAppPerSuite{

  val injector: Injector = app.injector
  implicit val mat: Materializer = app.injector.instanceOf[Materializer]

  lazy val messages: MessagesApi = injector.instanceOf[MessagesApi]
  lazy val mockConfig: AppConfig = injector.instanceOf[AppConfig]
  lazy val mockValidatedSession: ValidatedSession = injector.instanceOf[ValidatedSession]
  lazy val mockForm: VatFlatRateForm = app.injector.instanceOf[VatFlatRateForm]

  def createMockStateService(data: Option[VatFlatRateModel]): StateService = {

    val mockStateService = mock[StateService]

    when(mockStateService.fetchVatFlatRate()(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(data))

    mockStateService
  }


  "Calling the .turnover action" when {

    "there is no model in keystore" should {
      val noVatReturnModel = None

      lazy val mockStateService = createMockStateService(noVatReturnModel)
      lazy val request = FakeRequest("GET", "/").withSession(SessionKeys.sessionId -> s"any-old-id")
      val controller = new TurnoverController(mockConfig, messages, mockStateService, mockValidatedSession, mockForm)
      lazy val result = controller.turnover(request)

      "return 303" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "redirect to the landing page" in {
        redirectLocation(result) shouldBe Some(s"${routes.VatReturnPeriodController.vatReturnPeriod()}")
      }
    }

    "there is an annual model in keystore" should {

      val annualVatReturnPeriodModel = Some(VatFlatRateModel("annually", None, None))

      lazy val mockStateService = createMockStateService(annualVatReturnPeriodModel)
      lazy val request = FakeRequest("GET", "/").withSession(SessionKeys.sessionId -> s"any-old-id")
      val controller = new TurnoverController(mockConfig, messages, mockStateService, mockValidatedSession, mockForm)
      lazy val result = controller.turnover(request)

      "return 200" in {
        status(result) shouldBe Status.OK
      }

      "navigate to the annual turnover page" in {
        Jsoup.parse(bodyOf(result)).title shouldBe messages("turnover.title")
      }

    }

    "there is a quarterly model in keystsore" should {

      val data = Some(VatFlatRateModel("quarterly", None, None))

      lazy val mockStateService = createMockStateService(data)
      lazy val request = FakeRequest("GET", "/").withSession(SessionKeys.sessionId -> s"any-old-id")
      val controller = new TurnoverController(mockConfig, messages, mockStateService, mockValidatedSession, mockForm)
      lazy val result = controller.turnover(request)
      "return 200" in {
        status(result) shouldBe Status.OK
      }

      "navigate to the quarterly turnover page" in {
        Jsoup.parse(bodyOf(result)).title shouldBe messages("turnover.title")
      }
    }
  }//end turnover action

  "Calling the .submitTurnover action" when {

    "there is an error with the form" should {

      "" in {

      }
    }

  }

}
