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
import models.VatFlatRateModel
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
  val mockVatFlatRateModel: Option[VatFlatRateModel] = Some(VatFlatRateModel("annual", Some(99.99), Some(9.99)))

  def mockedStateService(): StateService = {
    val mockConnector: KeystoreConnector = mock[KeystoreConnector]
    val testData: Option[VatFlatRateModel] = mockVatFlatRateModel
    val returnedCacheMap = CacheMap("vatReturnPeriod", Map("data" -> Json.toJson(testData)))

    when(mockConnector.fetchAndGetFormData[VatFlatRateModel](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(testData))

    when(mockConnector.saveFormData[VatFlatRateModel](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(returnedCacheMap))

    new StateService(mockConnector)
  }

  "Calling StateService .fetchData" should {
    "return a VatReturnPeriodModel when there is one in Keystore" in {
      val saveData: Option[VatFlatRateModel] = mockVatFlatRateModel
      val service = mockedStateService()
      val result: Future[Option[VatFlatRateModel]] = service.fetchVatFlatRate()

      await(result) shouldBe Some(VatFlatRateModel("annual", Some(99.99), Some(9.99)))
    }
  }
  "Calling StateService .saveData" should {

    "return a Cachemap with a valid response" in {
      val testData = VatFlatRateModel("annual", Some(99.99), Some(9.99))
      val returnedData = CacheMap("vatReturnPeriod", Map("data" -> Json.toJson(testData)))
      val service = mockedStateService()
      val result = service.saveVatFlatRate[VatFlatRateModel](testData)

      await(result) shouldBe returnedData
    }
  }

}
