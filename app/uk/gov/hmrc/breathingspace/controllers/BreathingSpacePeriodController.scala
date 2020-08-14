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

import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.breathingspace.config.AppConfig
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.Future

@Singleton()
class BreathingSpacePeriodController @Inject()(appConfig: AppConfig, cc: ControllerComponents)
    extends BackendController(cc) {

  def retrieve(id: String): Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(s"Hello world $id"))
  }

  def create(): Action[AnyContent] = Action.async { implicit request =>
    val result = request.body.asJson match {
      case Some(value) => Ok(value)
      case None => NotAcceptable("must send data")
    }

//    val result = request.body.asJson.fold(JsValue("must send data"))(Ok(_))
    Future.successful(result)
  }
}
