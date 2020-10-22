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
import scala.util.Try

import play.api.Logging
import play.api.http.Status._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.mvc._
import uk.gov.hmrc.breathingspaceifstub.utils.ControllerSupport
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

@Singleton()
class PeriodsController @Inject()(
  cc: ControllerComponents
)(implicit val ec: ExecutionContext)
    extends BackendController(cc) {

  import PeriodsController._

  def get(nino: String): Action[AnyContent] = Action.async { implicit request =>
    composeResponse(nino, getAcceptedNinoHandler)
  }

  def post(nino: String): Action[AnyContent] = Action.async { implicit request =>
    composeResponse(nino, withBodyAcceptedNinoHandler(CREATED, true))
  }

  def put(nino: String): Action[AnyContent] = Action.async { implicit request =>
    composeResponse(nino, withBodyAcceptedNinoHandler(OK, false))
  }
}

object PeriodsController extends ControllerSupport {
  def getAcceptedNinoHandler(nino: String)(implicit request: Request[AnyContent]): Future[Result] =
    nino match {
      case "AS000001" => sendResponse(OK, Some(jsonDataFromFile("singleBsPeriodFullPopulation.json")))
      case "AS000002" => sendResponse(OK, Some(jsonDataFromFile("singleBsPeriodPartialPopulation.json")))
      case "AS000003" => sendResponse(OK, Some(jsonDataFromFile("multipleBsPeriodsFullPopulation.json")))
      case "AS000004" => sendResponse(OK, Some(jsonDataFromFile("multipleBsPeriodsPartialPopulation.json")))
      case "AS000005" => sendResponse(OK, Some(jsonDataFromFile("multipleBsPeriodsMixedPopulation.json")))
      case _ => sendResponse(OK, Some(Json.parse("""{"periods" :[]}""")))
    }

  def withBodyAcceptedNinoHandler(
    httpSuccessCode: Int,
    addPeriodIdField: Boolean
  )(nino: String)(implicit request: Request[AnyContent]): Future[Result] =
    request.body.asJson match {
      case None =>
        sendResponse(BAD_REQUEST)

      case Some(jsValue) =>
        logger.info(s"BS-STUB >> REQUEST: = POST ${request.uri} BODY: = ${jsValue.toString()}")
        transformRequestJsonToResponseJson(jsValue, addPeriodIdField) match {
          case JsError(_) => sendResponse(BAD_REQUEST)
          case JsSuccess(jsObject, _) => sendResponse(httpSuccessCode, Some(jsObject))
        }
    }

  def transformRequestJsonToResponseJson(jsValue: JsValue, addPeriodIdField: Boolean): JsResult[JsObject] = {
    val attrTransformer = (__ \ "periods").json.update {
      __.read[JsArray].map {
        case JsArray(values) =>
          val updatedValues = values.map { period =>
            val retainedFields = period.as[JsObject].fields.filter(_._1 != "pegaRequestTimestamp")
            val additionalFields =
              if (addPeriodIdField) Seq(("periodID", JsString(UUID.randomUUID().toString))) else Seq.empty

            JsObject(additionalFields ++ retainedFields)
          }

          JsArray(updatedValues)
      }
    }

    jsValue.transform(attrTransformer)
  }

  def jsonDataFromFile(filename: String): JsValue = getJsonDataFromFile(s"periods/$filename")
}
