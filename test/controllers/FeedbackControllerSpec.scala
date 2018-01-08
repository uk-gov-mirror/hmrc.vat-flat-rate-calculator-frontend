/*
 * Copyright 2018 HM Revenue & Customs
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

import config.{AppConfig, WSHttp}
import helpers.ControllerTestSpec
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import play.api.http.Status
import play.api.i18n.MessagesApi
import play.api.mvc.RequestHeader
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.play.partials.{CachedStaticHtmlPartialRetriever, FormPartialRetriever}

import scala.concurrent.Future
import uk.gov.hmrc.http.{ HttpGet, HttpResponse }

class FeedbackControllerSpec extends ControllerTestSpec {
  val fakeRequest = FakeRequest("GET", "/")

  implicit val config: AppConfig = mockConfig
  implicit val messagesApi: MessagesApi = messages
  implicit val mockHttp: WSHttp = mock[WSHttp]

  def targetController: FeedbackController = {
    object TestController extends FeedbackController{
      override implicit val cachedStaticHtmlPartialRetriever: CachedStaticHtmlPartialRetriever = new CachedStaticHtmlPartialRetriever {
              override def httpGet: HttpGet = ???

              override def getPartialContent(url: String, templateParameters: Map[String, String], errorMessage: Html)(implicit request: RequestHeader): Html =
                Html("")
            }
            override implicit val formPartialRetriever: FormPartialRetriever = new FormPartialRetriever {
              override def crypto: (String) => String = ???

              override def httpGet: HttpGet = ???

              override def getPartialContent(url: String, templateParameters: Map[String, String], errorMessage: Html)(implicit request: RequestHeader): Html = Html("")
            }
    }
    TestController
  }

  "GET /feedback" should {
    lazy val target = targetController

    "return feedback page" in {
      lazy val result = target.show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "capture the referer in the session on initial session on the feedback load" in {
      lazy val result = target.show(fakeRequest.withHeaders("Referer" -> "ref"))
      status(result) shouldBe Status.OK
    }
  }

  "POST /feedback" should {
    lazy val target = targetController
    lazy val fakePostRequest = FakeRequest("POST", "/").withFormUrlEncodedBody("test" -> "test")

    "return form with thank you for valid selections" in {
      when(mockHttp.POSTForm[HttpResponse](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(
        Future.successful(HttpResponse(Status.OK, responseString = Some("1234"))))

      lazy val result = target.submit(fakePostRequest)
      status(result) shouldBe Status.SEE_OTHER
    }

    "return form with errors for invalid selections" in {
      when(mockHttp.POSTForm[HttpResponse](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(
        Future.successful(HttpResponse(Status.BAD_REQUEST, responseString = Some("<p>:^(</p>"))))
      lazy val result = target.submit(fakePostRequest)
      status(result) shouldBe Status.BAD_REQUEST
    }

    "return error for other http code back from contact-frontend" in {
      when(mockHttp.POSTForm[HttpResponse](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(
        Future.successful(HttpResponse(418))) // 418 - I'm a teapot
      lazy val result = target.submit(fakePostRequest)
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
    }

    "return internal server error when there is an empty form" in {
      when(mockHttp.POSTForm[HttpResponse](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(
        Future.successful(HttpResponse(Status.OK, responseString = Some("1234"))))

      lazy val result = target.submit(fakeRequest)
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
    }
  }

  "GET /register-your-company/thankyou" should {
    lazy val target = targetController
    "should return the thank you page" in {
      lazy val result = target.thankyou(fakeRequest)
      status(result) shouldBe Status.OK
    }
  }
}
