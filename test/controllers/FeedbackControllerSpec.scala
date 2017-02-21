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

//package controllers
//
//package controllers.feedback
//
//import helpers.ControllerTestSpec
//import play.api.http.Status
//import play.api.i18n.MessagesApi
//import play.api.test.FakeRequest
//import play.api.test.Helpers._
//
//class FeedbackControllerSpec extends ControllerTestSpec {
//
//  val fakeRequest = FakeRequest("GET", "/")
//
//  val controller = new FeedbackController(mockConfig, messages, mockValidatedSession, mockStateService)
//
//  "GET /start" should {
//    "return 200" in new Setup {
//      val result = controller.show(fakeRequest)
//      status(result) shouldBe Status.OK
//      contentType(result) shouldBe Some("text/html")
//      charset(result) shouldBe Some("utf-8")
//    }
//  }
//
//}
