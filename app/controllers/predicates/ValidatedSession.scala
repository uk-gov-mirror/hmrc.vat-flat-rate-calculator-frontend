/*
 * Copyright 2019 HM Revenue & Customs
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

package controllers.predicates

import javax.inject.Inject
import config.AppConfig
import forms.VatFlatRateForm
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import views.html.{home => views}

import scala.concurrent.Future
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

class ValidatedSession @Inject()(config: AppConfig,
                                 mcc: MessagesControllerComponents,
                                 forms: VatFlatRateForm) extends FrontendController(mcc) with I18nSupport{

  private type PlayRequest = Request[AnyContent] => Result
  private type AsyncRequest = Request[AnyContent] => Future[Result]

  def async(action: AsyncRequest): Action[AnyContent] = {
    Action.async { implicit request =>
      if(request.session.get(SessionKeys.sessionId).isEmpty) {
        Logger.warn("No session ID found; timing out")
        Future.successful(Redirect(controllers.routes.TimeoutController.timeout()))
      } else {
       action(request)
      }
    }
  }
}