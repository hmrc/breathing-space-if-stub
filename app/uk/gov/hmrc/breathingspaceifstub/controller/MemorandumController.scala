/*
 * Copyright 2022 HM Revenue & Customs
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

import play.api.libs.json._
import play.api.mvc._
import uk.gov.hmrc.breathingspaceifstub.utils.ControllerSupport
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class MemorandumController @Inject()(
  cc: ControllerComponents
)(implicit val ec: ExecutionContext)
    extends BackendController(cc)
    with ControllerSupport {

  def get(nino: String): Action[AnyContent] = Action.async { implicit request =>
    composeResponse(nino, getAcceptedNinoHandler)
  }

  def getAcceptedNinoHandler(nino: String)(implicit request: Request[_]): Future[Result] =
    nino match {
      case "AS000001" => sendResponse(OK, jsonDataFromFile("hasBreathingSpaceIndicator.json"))
      case "AS000002" => sendResponse(OK, jsonDataFromFile("noBreathingSpaceIndicator.json"))
      case "AA000333" => sendResponse(OK, jsonDataFromFile("hasBreathingSpaceIndicator.json"))
      case "AS000003" => sendResponse(UNPROCESSABLE_ENTITY, failures("UNKNOWN_DATA_ITEM"))
      case "AS000004" => sendResponse(BAD_GATEWAY, failures("BAD_GATEWAY"))
      case _ => sendResponse(NOT_FOUND, failures("NO_DATA_FOUND", "No records found for the given Nino"))
    }

  def jsonDataFromFile(filename: String): JsValue = getJsonDataFromFile(s"memorandum/$filename")
}
