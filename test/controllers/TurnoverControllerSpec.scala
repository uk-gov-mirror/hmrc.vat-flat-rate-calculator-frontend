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
import play.api.test.FakeRequest
import services.StateService
import uk.gov.hmrc.play.http.SessionKeys
import uk.gov.hmrc.play.test.UnitSpec
import play.api.test.Helpers._

import scala.concurrent.Future

class TurnoverControllerSpec extends UnitSpec with MockitoSugar with ScalaFutures with OneAppPerSuite{

  val injector: Injector = app.injector
  implicit val mat: Materializer = app.injector.instanceOf[Materializer]

//  lazy val messages: MessagesApi = injector.instanceOf[MessagesApi]
//  lazy val mockConfig: AppConfig = injector.instanceOf[AppConfig]
//  lazy val mockValidatedSession: ValidatedSession = injector.instanceOf[ValidatedSession]
//  lazy val mockForm: VatFlatRateForm = app.injector.instanceOf[VatFlatRateForm]

  val mockVatReturnPeriodModel = Some(VatFlatRateModel("annual", Some(999.99), None))
  val mockNoVatReturnPeriodModel = None

//  def createMockStateService(data: Option[VatFlatRateModel]): StateService = {
//
//    val mockStateService = mock[StateService]
//
//    when(mockStateService.fetchVatFlatRate()(ArgumentMatchers.any(), ArgumentMatchers.any()))
//      .thenReturn(Future.successful(data))
//
//    mockStateService
//  }

  private def fixture = new {
    val mockMessages: MessagesApi = mock[MessagesApi]
    val mockConfig: AppConfig     = mock[AppConfig]
    val mockStateService: StateService = mock[StateService]
    val mockValidatedSession: ValidatedSession = mock[ValidatedSession]
    val mockForm: VatFlatRateForm = mock[VatFlatRateForm]

    val controller = new TurnoverController(mockConfig, mockMessages, mockStateService, mockValidatedSession, mockForm)

  }




  "Calling the .turnover action" when {

    "there is no session ID" should {
      val f = fixture
      lazy val mockStateService = createMockStateService(mockVatReturnPeriodModel)
      lazy val request = FakeRequest("GET", "/")

      val controller = new TurnoverController(mockConfig, messages, mockStateService, mockValidatedSession, mockForm)
      lazy val result = controller.turnover(request)

      "return 303" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "redirect to the landing page" in {
        redirectLocation(result) shouldBe Some(s"${routes.VatReturnPeriodController.vatReturnPeriod()}")
      }
    }

    "there is no model in keystore" should {
      lazy val mockStateService = createMockStateService(mockNoVatReturnPeriodModel)
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

    "for an annual vat return period" should {

      "return 200" in {

      }

      "navigate to the annual turnover page" in {

      }

    }

    "navigate to the turnover page or a quarterly vat return period" should {

      "return 200" in {

      }

      "navigate to the quarterly turnover page" in {

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
