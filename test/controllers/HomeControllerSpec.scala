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
import connectors.KeystoreConnector
import controllers.predicates.ValidatedSession
import helpers.FakeRequestTo
import org.scalatest.mock.MockitoSugar
import play.api.http.{HttpEntity, Status}
import play.api.i18n.MessagesApi
import play.api.inject.Injector
import play.api.test.Helpers._
import services.StateService
import uk.gov.hmrc.play.http.SessionKeys
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future


class HomeControllerSpec extends UnitSpec with WithFakeApplication with MockitoSugar{

  lazy val injector: Injector = fakeApplication.injector
  lazy val messages: MessagesApi = injector.instanceOf[MessagesApi]

  val mockConfig: AppConfig = injector.instanceOf[AppConfig]
  val mockStateService: StateService = injector.instanceOf[StateService]
  val mockKeystore: KeystoreConnector = injector.instanceOf[KeystoreConnector]
  val mockValidatedSession: ValidatedSession = injector.instanceOf[ValidatedSession]

  val target = new HomeController(mockConfig, messages, mockStateService, mockValidatedSession)

  "Navigating to the landing page" when {

    val sessionId =  UUID.randomUUID().toString

    "there is no sessionId" should {
      "generate a sessionId and continue" in {
        object DataItem extends FakeRequestTo("/", target.welcome, None)
        status(DataItem.result) shouldBe Status.OK
      }
    }
    "there is a sessionId" should {
      "return 200 " in {
        object DataItem extends FakeRequestTo("/", target.welcome, Some(sessionId))
        status(DataItem.result) shouldBe Status.OK
      }
    }
  }

  "Navigating to page 1" when {
    val sessionId =  UUID.randomUUID().toString

    "there is no sessionId" should {
      "generate a sessionId and go back to the landing page" in {
        object DataItem extends FakeRequestTo("/", target.welcome, None)
        status(DataItem.result) shouldBe Status.OK
      }
    }
    "there is a sessionId" should {
      "return 200 " in {
        object DataItem extends FakeRequestTo("/", target.pageOne, Some(sessionId))
        status(DataItem.result) shouldBe Status.OK
      }
    }

  }



}
