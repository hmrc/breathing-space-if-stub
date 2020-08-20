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

import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.Helpers._

class BreathingSpaceControllerSpec extends BaseControllerSpec {

  "POST to flow 6" should {
    "return 200" in {
      val result = controller.flow6(fakeRequest.withBody(Json.toJson(UUID.randomUUID.toString)))
      status(result) shouldBe Status.OK
      val output = contentAsString(result).drop(1).dropRight(1)
      UUID.fromString(output) shouldBe a[UUID]
    }

    "return 400" in {
      val result = controller.flow6(fakeRequest.withBody("{:}").withHeaders(CONTENT_TYPE -> JSON))
      status(result) shouldBe Status.BAD_REQUEST
    }

    "return 415" in {
      val result = controller.flow6(fakeRequest)
      status(result) shouldBe Status.UNSUPPORTED_MEDIA_TYPE
    }
  }

  "PUT to flow 12" should {
    "return 200" in {
      val result = controller.flow12(fakeRequest.withBody(Json.toJson("")))
      status(result) shouldBe Status.OK
    }

    "return 400" in {
      val result = controller.flow12(fakeRequest.withBody("{:}").withHeaders(CONTENT_TYPE -> JSON))
      status(result) shouldBe Status.BAD_REQUEST
    }

    "return 415" in {
      val result = controller.flow14a(fakeRequest)
      status(result) shouldBe Status.UNSUPPORTED_MEDIA_TYPE
    }
  }

  "DELETE to flow 14a" should {
    "return 200" in {
      val result = controller.flow14a(fakeRequest.withBody(Json.toJson("")))
      status(result) shouldBe Status.OK
    }

    "return 400" in {
      val result = controller.flow14a(fakeRequest.withBody("{:}").withHeaders(CONTENT_TYPE -> JSON))
      status(result) shouldBe Status.BAD_REQUEST
    }

    "return 415" in {
      val result = controller.flow14a(fakeRequest)
      status(result) shouldBe Status.UNSUPPORTED_MEDIA_TYPE
    }
  }
}
