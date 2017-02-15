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
import models.VatReturnPeriodModel
import play.api.libs.json.Format
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

@Singleton
class StateService @Inject()(keystore: KeystoreConnector) {

  def saveVatReturnPeriod[VatReturnPeriodModel](data: VatReturnPeriodModel)(implicit hc: HeaderCarrier, format: Format[VatReturnPeriodModel]): Future[CacheMap] = {
    keystore.saveFormData(common.CacheKeys.vatReturnPeriod.toString, data)
  }

  def fetchVatReturnPeriod()(implicit hc: HeaderCarrier, format: Format[VatReturnPeriodModel]): Future[Option[VatReturnPeriodModel]] = {
    keystore.fetchAndGetFormData(common.CacheKeys.vatReturnPeriod.toString)
  }
}
