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

package common

object Links {

  final val baseGovUrl    = "http://www.gov.uk/"
  final val taxServiceUrl = "https://www.tax.service.gov.uk/"

  val businessTaxAccount: String  = taxServiceUrl + "gg/sign-in?continue=https%3A%2F%2Fcustoms.hmrc.gov.uk%2Feprompt%2Fhttpssl%2FaddGeneralEmailAddress.do"
  val flatRateScheme: String      = baseGovUrl + "vat-flat-rate-scheme/join-or-leave-the-scheme"
  val vatRegistration: String     = baseGovUrl + "vat-registration/cancel-registration"

}
