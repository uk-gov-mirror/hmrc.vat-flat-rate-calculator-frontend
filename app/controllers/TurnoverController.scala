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

import javax.inject.{Inject, Singleton}

import config.AppConfig
import controllers.predicates.ValidatedSession
import forms.VatFlatRateForm
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import services.StateService
import uk.gov.hmrc.play.frontend.controller.FrontendController
import views.html.{home => views}

import scala.concurrent.Future

@Singleton
class TurnoverController @Inject()(config: AppConfig,
                                   val messagesApi: MessagesApi,
                                   stateService: StateService,
                                   session: ValidatedSession,
                                   forms: VatFlatRateForm) extends FrontendController with I18nSupport{

  val turnover: Action[AnyContent] = session.async{ implicit request =>
    for {
      vatReturnPeriod <- stateService.fetchVatFlatRate()
    } yield vatReturnPeriod match {
      case Some(model) =>
        model.vatReturnPeriod match {
          case s  if s.equalsIgnoreCase(Messages("vatReturnPeriod.option.annual"))    => Ok(views.turnover(config, forms.turnoverForm.fill(model), Messages("common.year")))
          case s  if s.equalsIgnoreCase(Messages("vatReturnPeriod.option.quarter"))   => Ok(views.turnover(config, forms.turnoverForm.fill(model), Messages("common.quarter")))
      }
      case _ => Redirect(controllers.routes.VatReturnPeriodController.vatReturnPeriod())
    }
  }

  val submitTurnover: Action[AnyContent] = session.async { implicit request =>
    forms.turnoverForm.bindFromRequest.fold(
      errors => {
        //TODO: Do we want to do this again?
        for {
          vatReturnPeriod <- stateService.fetchVatFlatRate()
        } yield vatReturnPeriod match {
          case Some(model) =>
            model.vatReturnPeriod match {
              case s  if s.equalsIgnoreCase(Messages("vatReturnPeriod.option.annual"))    => BadRequest(views.turnover(config, errors, Messages("common.year")))
              case s  if s.equalsIgnoreCase(Messages("vatReturnPeriod.option.quarter"))   => BadRequest(views.turnover(config, errors, Messages("common.year")))
            }
        }
      },
      success => {
        stateService.saveVatFlatRate(success)
        Future.successful(Redirect(controllers.routes.CostOfGoodsController.costOfGoods()))
      }
    )
  }

}
