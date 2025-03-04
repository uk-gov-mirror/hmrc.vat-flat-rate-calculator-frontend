/*
 * Copyright 2021 HM Revenue & Customs
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

package helpers.ViewSpecHelpers

import org.scalatest.mockito.MockitoSugar
import play.api.i18n.Lang

trait CostOfGoodsViewMessages extends MockitoSugar {
  implicit val lang: Lang = Lang("en")
  val costOfGoodTitle                      = "Enter your cost of goods"
  def costOfGoodsHeading(period : String)  = s"Enter your cost of goods for the $period including VAT"
  val costOfGoodsParagraph1                = "Goods are moveable items or materials exclusively used in your business. You can also include gas and electricity. If you're estimating, give realistic figures."
  val costOfGoodDontInclude                = "Don't include: "
  val costOfGoodsBullet1                   = "any services - which is anything that isn't goods"
  val costOfGoodsBullet2                   = "expenses like travel and accommodation"
  val costOfGoodsBullet3                   = "food and drink eaten by yourself or your staff"
  val costOfGoodsBullet4                   = "vehicle costs including fuel unless you're in the transport business using your own, or a leased vehicle"
  val costOfGoodsBullet5                   = "rent, internet, phone bills and accountancy fees"
  val costOfGoodsBullet6                   = "gifts, promotional items and donations"
  val costOfGoodsBullet7                   = "goods you will resell or hire out unless this is your main business activity"
  val costOfGoodsBullet8                   = "training and memberships"
  val costOfGoodsBullet9                   = "capital items for example office equipment, laptops, mobile phones and tablets"
  val costOfGoodsParagraph2                = "You can find out more about the VAT Flat Rate Scheme (opens in a new window) including examples of goods, services and how often to use this tool."
  val costOfGoodsError                     = "Enter an amount for costs"
  val costOfGoodsContinue                  = "Continue"
}
