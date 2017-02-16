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
import services.StateService
import uk.gov.hmrc.play.frontend.controller.FrontendController
import views.html.{home => views}

class ResultController @Inject()(config: AppConfig,
                                val messagesApi: MessagesApi,
                                stateService: StateService,
                                session: ValidatedSession) extends FrontendController with I18nSupport {

  def result = session.async{ implicit request =>
    for {
      Some(model) <- stateService.fetchResultModel()
      //TODO: Handle future filter predicate for None
    } yield model.result  match {
      case 1   => Ok(views.result(config, 1))
      case 2   => Ok(views.result(config, 2))
      case 3   => Ok(views.result(config, 3))
      case 4   => Ok(views.result(config, 4))
      case 5   => Ok(views.result(config, 5))
      case 6   => Ok(views.result(config, 6))
    }
  }
}
