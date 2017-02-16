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
import models.VatFlatRateModel
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import services.StateService
import uk.gov.hmrc.play.frontend.controller.FrontendController
import views.html.{home => views}

import scala.concurrent.Future

@Singleton
class CostOfGoodsController @Inject()(config: AppConfig,
                                   val messagesApi: MessagesApi,
                                   stateService: StateService,
                                   session: ValidatedSession,
                                   forms: VatFlatRateForm) extends FrontendController with I18nSupport{

  val costOfGoods: Action[AnyContent] = session.async{ implicit request =>
    for {
      vatReturnPeriod <- stateService.fetchVatFlateRate()
    } yield vatReturnPeriod match {
      case Some(model) => Ok(views.costOfGoods(config, forms.costOfGoodsForm.fill(model)))
      case _ => /*Todo handle No Model response**/Ok("")
    }
  }

  val submitCostOfGoods: Action[AnyContent] = session.async { implicit request =>
    forms.turnoverForm.bindFromRequest.fold(
      errors => Future.successful(BadRequest(views.costOfGoods(config, errors))),
      success => {
        for {
          vatReturnPeriod <- stateService.fetchVatFlateRate()
        } yield vatReturnPeriod match {
          case Some(model) => stateService.saveVatFlateRate(success)
            println(s"\n\n${whichResult(model)}\n\n")
            Future.successful(Ok("")) //Future.successful(Redirect(controllers.routes.ResultController.result(whichResult(model))))
          case _ => /*Todo handle No Model response**/Ok("")
        }
        stateService.saveVatFlateRate(success)
        Future.successful(Ok(""))
      }
    )
  }

  def whichResult(model: VatFlatRateModel): Int = {
    if(model.vatReturnPeriod == "annually"){
      model.turnover match {
        case Some(x) if x <= 1000 => 1
        case Some(x) if x*0.02 >= model.costOfGoods.getOrElse(0) => 2 //TODO what the get or else should be
        case _ => 3
      }
    } else {
      model.turnover match {
        case Some(x) if x <= 250 => 4
        case Some(x) if x*0.02 >= model.costOfGoods.getOrElse(0) => 5 //TODO what the get or else should be
        case _ => 6
      }
    }
  }

}