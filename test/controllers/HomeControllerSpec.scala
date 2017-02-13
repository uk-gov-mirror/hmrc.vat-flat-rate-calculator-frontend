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

import config.AppConfig
import connectors.KeystoreConnector
import org.scalatest.mock.MockitoSugar
import play.api.http.Status
import play.api.i18n.MessagesApi
import play.api.inject.Injector
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentType, _}
import services.StateService
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}


class HomeControllerSpec extends UnitSpec with WithFakeApplication with MockitoSugar{

  lazy val injector: Injector = fakeApplication.injector
  lazy val messages: MessagesApi = injector.instanceOf[MessagesApi]

  val fakeRequest = FakeRequest("GET", "/")
  val mockConfig: AppConfig = injector.instanceOf[AppConfig]
  val mockStateService: StateService = injector.instanceOf[StateService]
  val mockKeystore: KeystoreConnector = injector.instanceOf[KeystoreConnector]

  val target = new HomeController(mockConfig, messages, mockStateService)

  "GET /" should {
    "return 200" in {
      val result = target.welcome(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = target.welcome(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }


  }


}
