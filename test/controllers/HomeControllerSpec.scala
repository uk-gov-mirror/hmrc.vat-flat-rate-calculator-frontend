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

import config.{AppConfig, VfrSessionCache}
import connectors.KeystoreConnector
import controllers.predicates.ValidatedSession
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import play.api.http.Status
import play.api.i18n.MessagesApi
import play.api.inject.Injector
import play.api.test.FakeRequest
import services.StateService
import uk.gov.hmrc.play.http.SessionKeys
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future


class HomeControllerSpec extends UnitSpec with WithFakeApplication with MockitoSugar {

  val injector: Injector = fakeApplication.injector
  lazy val messages: MessagesApi = injector.instanceOf[MessagesApi]

  val mockSessionCache: VfrSessionCache = injector.instanceOf[VfrSessionCache]
  val mockConfig: AppConfig = injector.instanceOf[AppConfig]
  val mockStateService: StateService = injector.instanceOf[StateService]
  val mockKeyStoreConnector: KeystoreConnector = injector.instanceOf[KeystoreConnector]
  val mockValidatedSession: ValidatedSession = injector.instanceOf[ValidatedSession]

  def createMockKeyStore[T](data: Option[T]): StateService = {

    val mockStateService = mock[StateService]

    when(mockStateService.fetchData[T](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(data))

    mockStateService
  }


  val target = new VatReturnPeriodController(mockConfig, messages, mockStateService, mockValidatedSession)

    "Navigating to the landing page" when {

    "there is no sessionId" should {
      lazy val request = FakeRequest("GET", "/")
      lazy val result = target.welcome(request)

      "generate a sessionId and continue" in {
        status(result) shouldBe Status.OK
      }
    }
    "there is a sessionId" should {
      val sessionId =  UUID.randomUUID().toString
      lazy val request = FakeRequest("GET", "/").withSession(SessionKeys.sessionId -> s"sessionId-$sessionId")
      lazy val result = target.welcome(request)

      "return 200 " in {
        status(result) shouldBe Status.OK
      }
    }
  }

  "Navigating to page 1" when {
    val sessionId =  UUID.randomUUID().toString

    "there is no sessionId" should {
      lazy val request = FakeRequest("GET", "/")
      lazy val result = target.pageOne(request)
      "generate a sessionId and go back to the landing page" in {

        status(result) shouldBe Status.OK
      }
    }
    "there is a sessionId" should {
      lazy val request = FakeRequest("GET", "/check-your-vat-flat-rate/page-1").withSession(SessionKeys.sessionId -> s"$sessionId")
      lazy val result = target.pageOne(request)
      "return 200 " in {
        status(result) shouldBe Status.OK
      }
    }

    "there is an item in keystore" should {

      lazy val request = FakeRequest("GET", "/check-your-vat-flat-rate/page-1").withSession(SessionKeys.sessionId -> s"$sessionId")
      val service = createMockKeyStore(Some(1))
      val target= new VatReturnPeriodController(mockConfig, messages, service, mockValidatedSession)
      lazy val result = target.pageOne(request)

      "return 200 " in {
        status(result) shouldBe Status.OK
      }
    }
  }



}
