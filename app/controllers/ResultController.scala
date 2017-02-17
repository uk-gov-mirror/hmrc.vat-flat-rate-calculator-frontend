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

package controllers

import javax.inject.Inject

import common.ResultCodes
import config.AppConfig
import controllers.predicates.ValidatedSession
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Result}
import services.StateService
import uk.gov.hmrc.play.frontend.controller.FrontendController
import views.html.{home => views, errors}

class ResultController @Inject()(config: AppConfig,
                                val messagesApi: MessagesApi,
                                stateService: StateService,
                                session: ValidatedSession) extends FrontendController with I18nSupport {

  val result: Action[AnyContent] = session.async { implicit request =>
    stateService.fetchResultModel.map {
      case Some(model) => routeResult(model.result)
      case None => InternalServerError(errors.technicalError(config))
    }
  }

  def routeResult(code: Int): Result = {
    Ok(s"Result Code: $code")
  }
}
