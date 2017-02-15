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

import java.util.UUID
import javax.inject.{Inject, Singleton}

import config.AppConfig
import controllers.predicates.ValidatedSession
import forms.VatReturnPeriodForm
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services.StateService
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.SessionKeys
import views.html.{home => views}

import scala.concurrent.Future


@Singleton
class VatReturnPeriodController @Inject()(config: AppConfig,
                                          val messagesApi: MessagesApi,
                                          stateService: StateService,
                                          session: ValidatedSession,
                                          form: VatReturnPeriodForm) extends FrontendController with I18nSupport{

  val vatReturnPeriod: Action[AnyContent] = Action.async { implicit request =>

    if(request.session.get(SessionKeys.sessionId).isEmpty) {
      val sessionId = UUID.randomUUID().toString
      Future.successful(Ok(views.vatReturnPeriod(config, form.vatReturnPeriodForm)).withSession(SessionKeys.sessionId -> s"session-$sessionId"))
    } else {
      Future.successful(Ok(views.vatReturnPeriod(config, form.vatReturnPeriodForm)))
    }
  }

  val submitVatReturnPeriod: Action[AnyContent] = session.async{ implicit request =>

    println(s"\n\n${common.CacheKeys.vatReturnPeriod.toString}\n\n")

    form.vatReturnPeriodForm.bindFromRequest.fold(
      errors =>  Future.successful(BadRequest(views.vatReturnPeriod(config, errors))),
      success => {
        stateService.saveVatReturnPeriod(success)
        //TODO: Redirect to turnover page
        Future.successful(Ok(""))
      }
    )
  }
}
