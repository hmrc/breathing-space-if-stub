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

package uk.gov.hmrc.breathingspaceifstub.model

import enumeratum._
import play.api.libs.json.{JsObject, Json, Writes}

sealed abstract class Error(val message: String) extends EnumEntry

object Error extends Enum[Error] {

  implicit val writes = new Writes[Error] {
    def writes(errorItem: Error): JsObject =
      Json.obj(
        "code" -> errorItem.entryName,
        "reason" -> errorItem.message
      )
  }

  def fromThrowable(throwable: Throwable): JsObject =
    Json.obj(
      "code" -> "SERVER_ERROR",
      "message" -> throwable.getMessage
    )

  case object BAD_GATEWAY extends Error("Dependent systems are currently not responding.")
  case object INVALID_CORRELATIONID extends Error("Submission has not passed validation. Invalid header CorrelationId.")
  case object INVALID_ORIGINATORID extends Error("Submission has not passed validation. Invalid header OriginatorId.")
  case object INVALID_USERID extends Error("Submission has not passed validation. Invalid header UserId.")
  case object HEADERS_PRECONDITION_NOT_MET extends Error("Submission has not passed validation. Invalid headers.")
  case object INVALID_PAYLOAD extends Error("Submission has not passed validation. Invalid payload.")
  case object RESOURCE_NOT_FOUND extends Error("The remote endpoint has indicated that the resource was not found.")
  case object SERVER_ERROR extends Error("IF is experiencing problems that require live service intervention.")
  case object SERVICE_UNAVAILABLE extends Error("Dependent systems are currently not responding.")
  case object UNPROCESSABLE_ENTITY extends Error("Submission cannot be processed. The resource already exists.")
  case object UNSUPPORTED_MEDIA_TYPE extends Error("Content-type should be \"application/json\".")

  override val values = findValues
}
