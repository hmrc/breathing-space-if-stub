/*
 * Copyright 2020 HM Revenue & Customs
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

import java.util.UUID
import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.breathingspaceifstub.repository.DebtorRepository
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

@Singleton()
class BreathingSpaceController @Inject()(cc: ControllerComponents)(implicit val ec: ExecutionContext)
    extends BackendController(cc)
    with BaseController {

  def retrieveDebtorDetails(nino: String): Action[AnyContent] = Action.async { implicit request =>
    lazy val debtorNotFound: Result = {
      val message = "Unknown Nino"
      logger.error(message)
      NotFound(message)
    }

    lazy val debtorFound: JsValue => Result = (debtor: JsValue) => {
      logger.debug(s"Sending Debtor($debtor)")
      Ok(Json.toJson(debtor)).withHeaders(CONTENT_TYPE -> "application/json")
    }

    exceptionHandler {
      withValidNino(nino) { _ =>
        logger.debug(s"Retrieving Debtor Details for Nino($nino)!")
        Future.successful {
          DebtorRepository.get(nino).fold(debtorNotFound)(debtorFound)
        }
      }
    }
  }

  def flow6: Action[JsValue] = Action.async(parse.json) { implicit request =>
    logger.debug(s"flow 6: ${request.toString} body=${request.body.toString}")
    Future.successful(Ok(Json.toJson(UUID.randomUUID.toString)))
  }

  def flow12: Action[JsValue] = Action.async(parse.json) { implicit request =>
    logger.debug(s"flow 12: ${request.toString} body=${request.body.toString}")
    Future.successful(Ok(""))
  }

  def flow14a: Action[JsValue] = Action.async(parse.json) { implicit request =>
    logger.debug(s"flow 14a: ${request.toString} body=${request.body.toString}")
    Future.successful(Ok(""))
  }
}
