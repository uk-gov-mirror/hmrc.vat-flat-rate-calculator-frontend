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

package helpers

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.ByteString
import config.AppConfig
import play.api.inject.Injector
import play.api.libs.streams.Accumulator
import play.api.mvc.{Action, AnyContent, AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import uk.gov.hmrc.play.http.SessionKeys
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

class FakeRequestTo(url: String, controllerAction: Action[AnyContent], sessionId: Option[String], data: (String, String)*) extends UnitSpec {
  implicit val system = ActorSystem("test")
  implicit def mat: Materializer = ActorMaterializer()
  protected val baseUrl = "/check-your-vat-flat-rate"
  val fakeRequest= constructRequest(url, sessionId)
  val result: Future[Result] = controllerAction(fakeRequest)

  def constructRequest(url:String, sessionId: Option[String]) = {
    sessionId match {
      case Some(sessId) => FakeRequest("GET", s"$baseUrl$url").withSession(SessionKeys.sessionId -> s"sessionId-$sessionId")
      case None => FakeRequest("GET", s"$baseUrl$url")
    }
  }

}
