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
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services.StateService
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.SessionKeys
import views.html.{home => views}

import scala.concurrent.Future


@Singleton
class HomeController @Inject()(config: AppConfig,
                               val messagesApi: MessagesApi,
                               keyStore: StateService,
                               session: ValidatedSession) extends FrontendController with I18nSupport{

  def welcome: Action[AnyContent] = Action.async { implicit request =>
    keyStore.saveData("test", 0)
    if (request.session.get(SessionKeys.sessionId).isEmpty) {
      val sessionId = UUID.randomUUID().toString
      Future.successful(Ok(views.welcome(config)).withSession(request.session + (SessionKeys.sessionId -> s"session-$sessionId")))
    } else {
      Future.successful(Ok(views.welcome(config)))
    }
  }

  def pageOne: Action[AnyContent] = session.async { implicit request =>

    keyStore.fetchData[Int]("test").flatMap {
      case Some(s) =>
        val num = s + 1
        keyStore.saveData("test", num)
        Future.successful(Ok(views.pageOne(config, num)))
      case None    => Future.successful(Ok(views.pageOne(config, 0)))
    }
  }

  def pageTwo: Action[AnyContent] = session.async { implicit request =>
    keyStore.fetchData[Int]("test").flatMap {
      case Some(p) =>
        val num = p + 1
        keyStore.saveData("test", num)
        Future.successful(Ok(views.pageTwo(config, num)))
      case None    => Future.successful(Ok(views.pageTwo(config, 0)))
    }
  }
}
