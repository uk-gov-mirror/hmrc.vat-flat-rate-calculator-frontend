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

import common.ResultCodes
import config.AppConfig
import controllers.predicates.ValidatedSession
import forms.VatFlatRateForm
import models.{ResultModel, VatFlatRateModel}
import play.api.Logger
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import services.StateService
import uk.gov.hmrc.play.frontend.controller.FrontendController
import views.html.{errors => errs, home => views}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class CostOfGoodsController @Inject()(config: AppConfig,
                                   val messagesApi: MessagesApi,
                                   stateService: StateService,
                                   session: ValidatedSession,
                                   forms: VatFlatRateForm) extends FrontendController with I18nSupport{

  val costOfGoods: Action[AnyContent] = session.async{ implicit request =>
    for {
      vfrModel <- stateService.fetchVatFlatRate()
    } yield vfrModel match {
      case Some(model) =>
        model.vatReturnPeriod match {
          case s if s.equalsIgnoreCase(Messages("vatReturnPeriod.option.annual")) => Ok(views.costOfGoods(config, forms.costOfGoodsForm.fill(model), Messages("common.year")))
          case s if s.equalsIgnoreCase(Messages("vatReturnPeriod.option.quarter")) => Ok(views.costOfGoods(config, forms.costOfGoodsForm.fill(model), Messages("common.quarter")))
        }
      case _ =>
        Logger.warn("No model found in Keystore; redirecting back to landing page")
        Redirect(controllers.routes.VatReturnPeriodController.vatReturnPeriod())
    }
  }

  val submitCostOfGoods: Action[AnyContent] = session.async { implicit request =>
    forms.costOfGoodsForm.bindFromRequest.fold(
      errors => {
        for {
          vfrModel <- stateService.fetchVatFlatRate()
        } yield vfrModel match {
          case Some(model)  =>
            model.vatReturnPeriod match {
              case s  if s.equalsIgnoreCase(Messages("vatReturnPeriod.option.annual"))    => BadRequest(views.costOfGoods(config, errors, Messages("common.year")))
              case s  if s.equalsIgnoreCase(Messages("vatReturnPeriod.option.quarter"))   => BadRequest(views.costOfGoods(config, errors, Messages("common.quarter")))
            }
          case _ =>
            Logger.warn("No VatFlatRateModel found in Keystore")
            InternalServerError(errs.technicalError(config))

        }
      },
      success => {
        for {
          _ <- stateService.saveVatFlatRate(success)
          result <- whichResult(success)
          _ <- stateService.saveResultModel(createResultModel(success,result))
          response <- Future.successful(Redirect(controllers.routes.ResultController.result()))
        } yield response
      }
    )
  }

  def whichResult(model: VatFlatRateModel): Future[Int] = {
    if(model.vatReturnPeriod.equalsIgnoreCase(Messages("vatReturnPeriod.option.annual"))){
      model match {
        case VatFlatRateModel(_,_,Some(cost)) if cost < 1000 => Future(ResultCodes.ONE)
        case VatFlatRateModel(_,Some(turnover),Some(cost)) if turnover*0.02 > cost => Future(ResultCodes.TWO)
        case _ => Future(ResultCodes.THREE)
      }
    } else {
      model match {
        case VatFlatRateModel(_,_,Some(cost)) if cost < 250 => Future(ResultCodes.FOUR)
        case VatFlatRateModel(_,Some(turnover),Some(cost)) if turnover*0.02 > cost => Future(ResultCodes.FIVE)
        case _ => Future(ResultCodes.SIX)
      }
    }
  }

  def createResultModel(model: VatFlatRateModel, resultCode: Int): ResultModel = {
    ResultModel(model, resultCode)
  }

}