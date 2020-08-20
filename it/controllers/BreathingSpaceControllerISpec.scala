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

package controllers

import java.util.UUID

import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.Helpers._

class BreathingSpaceControllerISpec extends BaseControllerISpec {

  "POST to /breathing-space/flow-6" should {

    "return 200" in {
      val data = Json.obj()
      val result = await(ws.url(s"http://localhost:$port/breathing-space/flow-6")
        .addHttpHeaders("Content-Type" -> "application/json")
        .post(data))

      result.status shouldBe Status.OK
      val output = result.body.drop(1).dropRight(1)
      UUID.fromString(output) shouldBe a[UUID]
    }

    "return 400" in {

      val result = await(ws.url(s"http://localhost:$port/breathing-space/flow-6")
        .addHttpHeaders("Content-Type" -> "application/json")
        .post(""))

      result.status shouldBe Status.BAD_REQUEST
    }

    "return 415" in {

      val result = await(ws.url(s"http://localhost:$port/breathing-space/flow-6")
        .post(""))

      result.status shouldBe Status.UNSUPPORTED_MEDIA_TYPE
    }


  }
}