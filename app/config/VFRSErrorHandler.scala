/*
 * Copyright 2021 HM Revenue & Customs
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

import javax.inject.Inject
import play.api.{Configuration, Play, mvc}
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.Request
import play.mvc.Http.Request
import play.twirl.api.Html
import uk.gov.hmrc.play.bootstrap.http.FrontendErrorHandler
import views.html.error_template

class VFRSErrorHandler @Inject()(val messagesApi: MessagesApi, val configuration: Configuration) extends FrontendErrorHandler {

  override def standardErrorTemplate(pageTitle: String, heading: String, message: String)(implicit request: mvc.Request[_]): Html = {
    val appConfig: AppConfig = Play.current.injector.instanceOf[AppConfig]
    error_template(pageTitle, heading, message, appConfig)
  }

  override def internalServerErrorTemplate(implicit request: mvc.Request[_]): Html =
    standardErrorTemplate(
      Messages("techError.title"),
      Messages("techError.heading"),
      Messages("techError.para.1")
    )

}
