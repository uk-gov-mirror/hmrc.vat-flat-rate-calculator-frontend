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

import helpers.ControllerTestSpec
import play.api.http.Status
import play.api.test.FakeRequest



class FeedbackSurveyControllerSpec extends ControllerTestSpec {

  "Feedback survey controller" must {
    "Redirect to feedback survey page when survey link is clicked" in {
      lazy val request = FakeRequest("GET", "/")
      lazy val controller = new FeedbackSurveyController(mockApplicationConfig)
      lazy val result = controller.redirectFeedbackSurvey(request)

      status(result) shouldBe Status.SEE_OTHER

    }
  }
}