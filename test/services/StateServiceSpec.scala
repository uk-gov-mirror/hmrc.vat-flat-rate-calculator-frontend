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

package services

import connectors.KeystoreConnector
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.Json
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

class StateServiceSpec extends UnitSpec with MockitoSugar{

  implicit val hc: HeaderCarrier = mock[HeaderCarrier]

  def mockedStateService(response: Option[String]): StateService = {
    val mockConnector: KeystoreConnector = mock[KeystoreConnector]
    val testData: Option[String] = response
    val returnedCacheMap = CacheMap("test", Map("data" -> Json.toJson(testData)))

    when(mockConnector.fetchAndGetFormData[String](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(Some("test")))

    when(mockConnector.saveFormData[String](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(returnedCacheMap))

    new StateService(mockConnector)
  }

  "Calling StateService .saveData" should {
    "return a Cachemap when supplied with valid data" in {
      val service = mockedStateService(Some("testData"))
      val result = service.fetchData[String]("test")

      await(result) shouldBe Some("test")
    }

    "return a String with a valid response" in {
      val service = mockedStateService(Some("test"))
      val result = service.saveData("test", "doesNotMatter")

      await(result) shouldBe CacheMap("test", Map("data" -> Json.toJson(Some("test"))))
    }
  }

}
