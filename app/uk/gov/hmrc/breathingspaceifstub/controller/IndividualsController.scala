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

import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

import play.api.http.Status.{NOT_FOUND, OK, UNPROCESSABLE_ENTITY}
import play.api.libs.json.JsValue
import play.api.mvc._
import uk.gov.hmrc.breathingspaceifstub.utils.ControllerSupport
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

@Singleton()
class IndividualsController @Inject()(
  cc: ControllerComponents
)(implicit val ec: ExecutionContext)
    extends BackendController(cc) {

  import IndividualsController._

  def get(nino: String, fields: String): Action[AnyContent] = Action.async { implicit request =>
    composeResponse(nino, getAcceptedNinoHandler(fields))
  }
}

object IndividualsController extends ControllerSupport {
  private val MinimumPopulation = "MinimumPopulation"

  def getAcceptedNinoHandler(fields: String)(nino: String)(implicit request: Request[AnyContent]): Future[Result] =
    (nino) match {
      case "AS000001" =>
        getAcceptedFilterHandler(fields, "FullPopulation")

      case "AS000002" =>
        getAcceptedFilterHandler(fields, MinimumPopulation)

      case "AS000003" =>
        getAcceptedFilterHandler(fields, "SingularPopulation")

      case _ => sendResponse(NOT_FOUND)
    }

  def getAcceptedFilterHandler(fields: String, population: String)(
    implicit request: Request[AnyContent]
  ): Future[Result] =
    fields.replaceAll("\\s+", "") match {
      case "details(nino,dateOfBirth,cnrIndicator)" =>
        sendResponse(OK, Some(jsonDataFromFile(getSampleDataFileNamePart(population, 0))))

      case "details(nino,dateOfBirth),namelist(name(firstForename,secondForename,surname))" =>
        sendResponse(OK, Some(jsonDataFromFile(getSampleDataFileNamePart(population, 1))))

      case _ =>
        sendResponse(UNPROCESSABLE_ENTITY)
    }

  def getSampleDataFileNamePart(fileName: String, filterIndex: Int): String =
    fileName match {
      case MinimumPopulation => s"${fileName}"
      case otherPopulation => s"${filterIndex}${fileName}"
    }

  def jsonDataFromFile(filename: String): JsValue = getJsonDataFromFile(s"individuals/details${filename}.json")
}
