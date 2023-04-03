/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.breathingspaceifstub.controller

import play.api.libs.json.JsValue
import play.api.mvc.{Action, AnyContent, ControllerComponents, Request, Result}
import uk.gov.hmrc.breathingspaceifstub.utils.ControllerSupport
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class UnderpaymentsController @Inject()(cc: ControllerComponents)(implicit val ec: ExecutionContext)
    extends BackendController(cc)
    with ControllerSupport {

  def get(nino: String, periodId: UUID): Action[AnyContent] = Action.async { implicit request =>
    val handlerWithPeriodId: String => Future[Result] = underpaymentHandler(periodId.toString) _
    composeResponse(nino, handlerWithPeriodId)
  }

  private def underpaymentHandler(periodId: String)(nino: String)(implicit request: Request[_]): Future[Result] = {

    def jsonDataFromFile(filename: String): JsValue = getJsonDataFromFile(s"underpayments/$filename")

    (nino, periodId) match {
      case ("AS000001", "648ea46e-8027-11ec-b614-03845253624e") =>
        sendResponse(OK, jsonDataFromFile("underpayments1.json"))
      case _ => sendResponse(NOT_FOUND, failures(s"NO_DATA_FOUND", s"$nino or $periodId did not match"))
    }
  }
}
