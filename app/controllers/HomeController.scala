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

import javax.inject.Singleton

import config.AppConfig
import views.html.{home => views}
import com.google.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

@Singleton
class HomeController @Inject()(configuration: AppConfig,
                               val messagesApi: MessagesApi) extends HomeControllerT {
  override val config: AppConfig = configuration
}

trait HomeControllerT extends FrontendController with I18nSupport {
  val config: AppConfig

  ////SETUP METHODS TODO: replace

  def welcome: Action[AnyContent] = Action.async { implicit request =>
        Future.successful(Ok(views.welcome(config,1)))
  }

  def pageTwo: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(views.pageTwo(config, 1)))
  }
}
