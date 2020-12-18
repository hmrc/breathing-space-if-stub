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

import scala.io.Source

import play.api.http.Status
import play.api.libs.ws.WSResponse
import play.api.test.Helpers.await
import uk.gov.hmrc.breathingspaceifstub.Header
import uk.gov.hmrc.breathingspaceifstub.controller.IndividualDetailsController._
import uk.gov.hmrc.breathingspaceifstub.model.CorrelationId
import uk.gov.hmrc.breathingspaceifstub.support.BaseISpec

class IndividualDetailsControllerISpec extends BaseISpec {

  implicit val correlationHeaderValue: CorrelationId = CorrelationId(Some(correlationId))

  "GET /NINO/:nino" should {
    "return 200(OK) with the expected individual details when the Url provides the expected filter" in {
      val nino = "AS000001"
      val response = makeGetRequest(getConnectionUrl(nino, Some(filter)))
      response.status shouldBe Status.OK
      response.body shouldBe getExpectedResponseBody(nino, detailsForBreathingSpace)
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 200(OK) with a individual details (full population) when the Url does not provide a filter" in {
      val nino = "AS000001"
      val response = makeGetRequest(getConnectionUrl(nino, None))
      response.status shouldBe Status.OK
      response.body shouldBe getExpectedResponseBody(nino, fullPopulationDetails)
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 400(BAD_REQUEST) when the Nino 'BS000400B' is sent" in {
      val response = makeGetRequest(getConnectionUrl("BS000400B"))
      response.status shouldBe Status.BAD_REQUEST
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 404(NOT_FOUND) when the Nino 'BS000404B' is sent" in {
      val response = makeGetRequest(getConnectionUrl("BS000404B"))
      response.status shouldBe Status.NOT_FOUND
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 422(UNPROCESSABLE_ENTITY) when the Url provides an unexpected filter" in {
      val response = makeGetRequest(getConnectionUrl("AS000001A", Some("details(nino,dateOfBirth,cnrIndicator)")))
      response.status shouldBe Status.UNPROCESSABLE_ENTITY
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 500(SERVER_ERROR) when the Nino 'BS000500B' is sent" in {
      val response = makeGetRequest(getConnectionUrl("BS000500B"))
      response.status shouldBe Status.INTERNAL_SERVER_ERROR
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 500(SERVER_ERROR) when the Nino 'BS0005R0B' is sent" in {
      val response = makeGetRequest(getConnectionUrl("BS0005R0B"))
      response.status shouldBe Status.INTERNAL_SERVER_ERROR
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 502(BAD_GATEWAY) when the Nino 'BS000502B' is sent" in {
      val response = makeGetRequest(getConnectionUrl("BS000502B"))
      response.status shouldBe Status.BAD_GATEWAY
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 503(SERVICE_UNAVAILABLE) when the Nino 'BS000503B' is sent" in {
      val response = makeGetRequest(getConnectionUrl("BS000503B"))
      response.status shouldBe Status.SERVICE_UNAVAILABLE
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 500(SERVER_ERROR) when the Nino specifies a non-existing HTTP status code" in {
      val response = makeGetRequest(getConnectionUrl("BS000700B"))
      response.status shouldBe Status.INTERNAL_SERVER_ERROR
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "ensure Nino suffix is ignored" in {
      withClue("With suffix") {
        val nino = "AS000001"
        val response = makeGetRequest(getConnectionUrl(s"${nino}A", Some(filter)))
        response.status shouldBe Status.OK
        response.body shouldBe getExpectedResponseBody(nino, detailsForBreathingSpace)
        response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
      }

      withClue("Without suffix") {
        val nino = "AS000001"
        val response = makeGetRequest(getConnectionUrl(nino, Some(filter)))
        response.status shouldBe Status.OK
        response.body shouldBe getExpectedResponseBody(nino, detailsForBreathingSpace)
        response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
      }
    }

    "return same CorrelationId as sent regardless of header name's letter case" in {
      withClue("Mixed case") {
        val response = await(wsClient.url(getConnectionUrl("AS000001", Some(filter)))
          .withHttpHeaders("CorrelationId" -> correlationHeaderValue.value.get)
          .get())
        response.status shouldBe Status.OK
        response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
      }

      withClue("Lower case") {
        val response = await(wsClient.url(getConnectionUrl("AS000001", Some(filter)))
          .withHttpHeaders("correlationid" -> correlationHeaderValue.value.get)
          .get())
        response.status shouldBe Status.OK
        response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
      }

      withClue("Upper case") {
        val response = await(wsClient.url(getConnectionUrl("AS000001", Some(filter)))
          .withHttpHeaders("CORRELATIONID" -> correlationHeaderValue.value.get)
          .get())
        response.status shouldBe Status.OK
        response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
      }
    }
  }

  private def makeGetRequest(connectionUrl: String)(implicit correlationId: CorrelationId): WSResponse =
    await(wsClient.url(connectionUrl).withHttpHeaders(Header.CorrelationId -> correlationId.value.get).get())

  private def getConnectionUrl(nino: String, filter: Option[String] = None): String = {
    val queryString = filter.map(value => s"?fields=${value}").getOrElse("")
    s"${testServerAddress}/individuals/details/NINO/${nino}${queryString}"
  }

  private def getExpectedResponseBody(nino: String, filename: String): String = {
    val in = getClass.getResourceAsStream(s"/data/individuals/$filename")
    Source.fromInputStream(in)
      .getLines
      .map( // remove pre padding whitespace & post colon whitespace from each line (but not whitespaces from values)
        _.replaceAll("^[ \\t]+", "")
          .replaceAll(":[ \\t]+", ":")
          .replaceFirst("\\$\\{nino}", nino)
      )
      .mkString
  }
}
