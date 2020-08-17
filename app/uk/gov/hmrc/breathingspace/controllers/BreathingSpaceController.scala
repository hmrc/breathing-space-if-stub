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

package uk.gov.hmrc.breathingspace.controllers

import java.util.UUID

import javax.inject.{Inject, Singleton}
import play.api.Logging
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.breathingspace.config.AppConfig
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.Future

@Singleton()
class BreathingSpaceController @Inject()(appConfig: AppConfig, cc: ControllerComponents)
    extends BackendController(cc) with Logging {

  def flow6: Action[JsValue] = Action.async(parse.json) { implicit request =>
    logger.info(s"flow 6: ${request.toString} body=${request.body.toString}")
    Future.successful(Ok(Json.toJson(UUID.randomUUID.toString)))
  }

  def flow12: Action[JsValue] = Action.async(parse.json) { implicit request =>
    logger.info(s"flow 12: ${request.toString} body=${request.body.toString}")
    Future.successful(Ok(""))
  }

  def flow14a: Action[JsValue] = Action.async(parse.json) { implicit request =>
    logger.info(s"flow 14a: ${request.toString} body=${request.body.toString}")
    Future.successful(Ok(""))
  }

}