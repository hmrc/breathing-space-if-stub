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

import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.hmrc.breathingspaceifstub.utils.ControllerSupport
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

@Singleton()
class IndividualDetailsController @Inject()(
  configuration: Configuration,
  cc: ControllerComponents
)(implicit val ec: ExecutionContext)
    extends BackendController(cc)
    with ControllerSupport {

  lazy val fullPopulationDetailsDisabled: Boolean =
    !configuration.getOptional[Boolean]("full-population-details-enabled").getOrElse(false)

  def get(nino: String, fields: Option[String]): Action[AnyContent] = Action.async { implicit request =>
    composeResponse(nino, getAcceptedNinoHandler(fields))
  }

  import IndividualDetailsController._

  private def getAcceptedNinoHandler(
    fields: Option[String]
  )(nino: String)(implicit request: Request[_]): Future[Result] =
    fields.fold {
      if (fullPopulationDetailsDisabled) sendResponse(BAD_REQUEST, failures("INVALID_ENDPOINT"))
      else sendResponse(nino, getDataFromFile(s"individuals/$fullPopulationDetails"))
    } { queryString =>
      val qs = queryString.replaceAll("\\s+", "")
      if (qs == filter) sendResponse(nino, getDataFromFile(s"individuals/$detailsForBreathingSpace"))
      else sendResponse(UNPROCESSABLE_ENTITY, failures("UNKNOWN_DATA_ITEM"))
    }

  private def sendResponse(nino: String, details: String)(implicit request: Request[_]): Future[Result] =
    sendResponse(OK, Json.parse(details.replaceFirst("\\$\\{nino}", nino)))
}

object IndividualDetailsController {

  val filter = {
    val Details = "details(nino,dateOfBirth)"
    val NameList = "nameList(name(firstForename,secondForename,surname,nameType))"
    val AddressList =
      "addressList(address(addressLine1,addressLine2,addressLine3,addressLine4,addressLine5,addressPostcode,countryCode,addressType))"
    val Indicators = "indicators(welshOutputInd)"

    s"$Details,$NameList,$AddressList,$Indicators"
  }

  val fullPopulationDetails = "IndividualDetails.json"
  val detailsForBreathingSpace = "IndividualDetailsForBS.json"
}
