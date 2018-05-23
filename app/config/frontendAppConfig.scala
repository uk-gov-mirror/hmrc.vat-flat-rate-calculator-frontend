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

package config

import javax.inject.{Inject, Singleton}
import play.api.Mode.Mode
import play.api.{Configuration, Environment}
import uk.gov.hmrc.play.config.ServicesConfig

trait AppConfig {
  val analyticsToken: String
  val analyticsHost: String
  val contactFormServiceIdentifier: String
  val contactFrontendPartialBaseUrl: String
  val reportAProblemPartialUrl: String
  val reportAProblemNonJSUrl: String
  val businessTaxAccount: String
  val urBannerLink: String
}

@Singleton
class ApplicationConfig @Inject()(override val runModeConfiguration: Configuration,
                                  environment: Environment) extends AppConfig with ServicesConfig {

  override protected def mode: Mode = environment.mode

  private def loadConfig(key: String): String = runModeConfiguration.getString(key).getOrElse(throw new Exception(s"Missing configuration key: $key"))

  private lazy val baseUrl = "check-your-vat-flat-rate"
  private lazy val contactHost = runModeConfiguration.getString(s"contact-frontend.host").getOrElse("")

  // Feedback Config
  private lazy val contactFrontendService = baseUrl("contact-frontend")
  override lazy val contactFormServiceIdentifier = "VFR"
  override lazy val contactFrontendPartialBaseUrl = s"$contactFrontendService"
  override lazy val reportAProblemPartialUrl: String = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  override lazy val reportAProblemNonJSUrl: String = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"

  //Business Tax Account
  override lazy val businessTaxAccount: String = runModeConfiguration.getString("business-tax-account.url").getOrElse("")

  // GA
  override lazy val analyticsToken: String = loadConfig(s"google-analytics.token")
  override lazy val analyticsHost: String = loadConfig(s"google-analytics.host")

  //Banner
  override val urBannerLink: String = "https://signup.take-part-in-research.service.gov.uk/?utm_campaign=VFRS_results&utm_source=Survey_Banner&utm_medium=other&t=HMRC&id=114"
}
