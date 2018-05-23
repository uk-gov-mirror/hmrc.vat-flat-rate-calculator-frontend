/*
 * Copyright 2018 HM Revenue & Customs
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

import config.AppConfig
import controllers.predicates.ValidatedSession
import javax.inject.Inject
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import services.StateService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.{home => views}

import scala.util.Random

class ResultController @Inject()(config: AppConfig,
                                val messagesApi: MessagesApi,
                                stateService: StateService,
                                session: ValidatedSession) extends FrontendController with I18nSupport {
  val result: Action[AnyContent] = session.async { implicit request =>
    val showUserResearchPanel = setURPanelFlag
    stateService.fetchResultModel.map {
      case Some(model)  => Ok(views.result(config, model.result, recordToGA(model.result), showUserResearchPanel))
      case None         =>
        Logger.warn("ResultModel could not be retrieved from Keystore")
        Redirect(controllers.routes.VatReturnPeriodController.vatReturnPeriod())
    }
  }

  def recordToGA(resultCode: Int): Array[String] = { // PASS THIS THROUGH IN PARAMETER OF VIEW
    resultCode match {
      case 1 => Array("Annual","use16.5%","under1000")
      case 2 => Array("Annual","use16.5%","costs<2%")
      case 3 => Array("Annual","useBFR","qualifyForBFR")
      case 4 => Array("Quarter","use16.5%","under250")
      case 5 => Array("Quarter","use16.5%","costs<2%")
      case 6 => Array("Quarter","useBFR","qualifyForBFR")
    }
  }
  private[controllers] def setURPanelFlag(implicit hc: HeaderCarrier): Boolean = {
    val random = new Random()
    val seed = getLongFromSessionID(hc)
    random.setSeed(seed)
    random.nextInt(3) == 0
  }

  private[controllers] def getLongFromSessionID(hc: HeaderCarrier): Long = {
    val session = hc.sessionId.map(_.value).getOrElse("0")
    val numericSessionValues = session.replaceAll("[^0-9]", "") match {
      case "" => "0"
      case num => num
    }
    numericSessionValues.takeRight(10).toLong
  }

}
