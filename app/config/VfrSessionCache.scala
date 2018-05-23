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
import play.api.{Configuration, Environment}
import uk.gov.hmrc.http.cache.client.SessionCache
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.config.{AppName, ServicesConfig}

@Singleton
class VfrSessionCache @Inject()(val http: HttpClient,
                                appConfig: AppConfig,
                                override val runModeConfiguration: Configuration,
                                environment: Environment) extends SessionCache with ServicesConfig with AppName {

  override val appNameConfiguration = runModeConfiguration

  override protected def mode = environment.mode

  override lazy val domain: String = getConfString("cachable.session-cache.domain", throw new Exception(""))
  override lazy val baseUri: String = baseUrl("cachable.session-cache")
  override lazy val defaultSource: String = appName
}
