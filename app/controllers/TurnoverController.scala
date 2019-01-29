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

package controllers

import javax.inject.{Inject, Singleton}
import config.AppConfig
import controllers.predicates.ValidatedSession
import forms.VatFlatRateForm
import models.VatFlatRateModel
import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, Request, Result}
import services.StateService
import views.html.{errors, home => views}

import scala.concurrent.Future
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

@Singleton
class TurnoverController @Inject()(config: AppConfig,
                                   val messagesApi: MessagesApi,
                                   stateService: StateService,
                                   session: ValidatedSession,
                                   forms: VatFlatRateForm) extends FrontendController with I18nSupport{

  val turnover: Action[AnyContent] = session.async{ implicit request =>
    routeRequest(Ok, forms.turnoverForm)
  }

  val submitTurnover: Action[AnyContent] = session.async { implicit request =>
  forms.turnoverForm.bindFromRequest.fold(
      errors => {
        Logger.info("Turnover form could not be bound")
        routeRequest(BadRequest, errors)
      },
      success => {
        stateService.saveVatFlatRate(success).map(
          _ => Redirect(controllers.routes.CostOfGoodsController.costOfGoods()))
      }
    )
  }

  def routeRequest(res: Status, form: Form[VatFlatRateModel])(implicit req: Request[AnyContent], hc: HeaderCarrier): Future[Result] = {
    for {
      vfrModel <- stateService.fetchVatFlatRate()
    } yield vfrModel match {
      case Some(model) =>
        model.vatReturnPeriod match {
          case s  if s.equalsIgnoreCase(Messages("vatReturnPeriod.option.annual"))    => res(views.turnover(config, form.fill(model), Messages("common.year")))
          case s  if s.equalsIgnoreCase(Messages("vatReturnPeriod.option.quarter"))   => res(views.turnover(config, form.fill(model), Messages("common.quarter")))
          case _ =>
            Logger.warn(
              s"""Incorrect value found for Vat Return Period:
                 |Should be [${Messages("vatReturnPeriod.option.annual")}] or [${Messages("vatReturnPeriod.option.quarter")}] but found ${model.vatReturnPeriod}""".stripMargin
            )
            InternalServerError(errors.technicalError(config))
        }
      case _ =>
        res match {
          case Ok =>
            Logger.warn("[Turnover Controller]No model found in Keystore; redirecting back to landing page")
            Redirect(controllers.routes.VatReturnPeriodController.vatReturnPeriod())
          case BadRequest =>
            Logger.warn("[Turnover Controller]No VatFlatRate model found in Keystore")
            InternalServerError(errors.technicalError(config))
        }
    }
  }

}
