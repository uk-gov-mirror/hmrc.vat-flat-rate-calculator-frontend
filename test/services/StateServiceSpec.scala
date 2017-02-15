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
import models.VatReturnPeriodModel
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

  def mockedStateService(): StateService = {
    val mockConnector: KeystoreConnector = mock[KeystoreConnector]
    val testData: Option[VatReturnPeriodModel] = Some(VatReturnPeriodModel("annual"))
    val returnedCacheMap = CacheMap("vatReturnPeriod", Map("data" -> Json.toJson(testData)))

    when(mockConnector.fetchAndGetFormData[VatReturnPeriodModel](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(testData))

    when(mockConnector.saveFormData[VatReturnPeriodModel](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(returnedCacheMap))

    new StateService(mockConnector)
  }

  "Calling StateService .fetchData" should {
    "return a VatReturnPeriodModel when there is one in Keystore" in {
      val saveData: Option[VatReturnPeriodModel] = Some(VatReturnPeriodModel("annual"))
      val service = mockedStateService()
      val result: Future[Option[VatReturnPeriodModel]] = service.fetchVatReturnPeriod()

      await(result) shouldBe Some(VatReturnPeriodModel("annual"))
    }
  }
  "Calling StateService .saveData" should {

    "return a Cachemap with a valid response" in {
      val testData = VatReturnPeriodModel("annual")
      val returnedData = CacheMap("vatReturnPeriod", Map("data" -> Json.toJson(testData)))
      val service = mockedStateService()
      val result = service.saveVatReturnPeriod[VatReturnPeriodModel](testData)

      await(result) shouldBe returnedData
    }
  }

}
