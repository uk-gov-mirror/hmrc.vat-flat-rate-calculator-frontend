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

import javax.inject.{Inject, Singleton}

import connectors.KeystoreConnector
import models.{ResultModel, VatFlatRateModel}
import play.api.libs.json.Format
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

@Singleton
class StateService @Inject()(keystore: KeystoreConnector) {

  def saveVatFlateRate[VatFlatRateModel](data: VatFlatRateModel)(implicit hc: HeaderCarrier, format: Format[VatFlatRateModel]): Future[CacheMap] = {
    keystore.saveFormData(common.CacheKeys.vatFlatRate.toString, data)
  }

  def fetchVatFlateRate()(implicit hc: HeaderCarrier, format: Format[VatFlatRateModel]): Future[Option[VatFlatRateModel]] = {
    keystore.fetchAndGetFormData(common.CacheKeys.vatFlatRate.toString)
  }

  def saveResultModel[ResultModel](data: ResultModel)(implicit hc: HeaderCarrier, format: Format[ResultModel]): Future[CacheMap] = {
    keystore.saveFormData(common.CacheKeys.vfrResult.toString, data)
  }

  def fetchResultModel()(implicit hc: HeaderCarrier, format: Format[ResultModel]): Future[Option[ResultModel]] = {
    keystore.fetchAndGetFormData(common.CacheKeys.vfrResult.toString)
  }
}
