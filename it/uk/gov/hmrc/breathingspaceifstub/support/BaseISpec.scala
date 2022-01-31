/*
 * Copyright 2022 HM Revenue & Customs
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

package uk.gov.hmrc.breathingspaceifstub.support

import java.util.UUID

import akka.stream.Materializer
import org.scalatest.OptionValues
import org.scalatest.concurrent.Futures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.http.HeaderNames.CONTENT_TYPE
import play.api.http.MimeTypes
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import play.api.mvc.AnyContentAsEmpty
import play.api.test.{DefaultAwaitTimeout, FakeRequest, Injecting}
import uk.gov.hmrc.breathingspaceifstub._
import uk.gov.hmrc.breathingspaceifstub.model.Attended

trait BaseISpec
  extends AnyWordSpec
    with DefaultAwaitTimeout
    with Futures
    with GuiceOneServerPerSuite
    with Injecting
    with Matchers
    with OptionValues {

  val configProperties: Map[String, Any] = Map(
    "full-population-details-enabled" -> true
  )

  override lazy val app: Application = GuiceApplicationBuilder().configure(configProperties).build()

  implicit lazy val materializer: Materializer = app.materializer

  lazy val wsClient: WSClient = inject[WSClient]

  lazy val testServerAddress = s"http://localhost:$port"

  lazy val correlationId = UUID.randomUUID().toString

  lazy val validHeaders = List(
    CONTENT_TYPE -> MimeTypes.JSON,
    Header.CorrelationId -> correlationId,
    Header.OriginatorId -> Attended.DA2_BS_ATTENDED.toString,
    Header.UserId -> "1234567"
  )

  def requestWithHeaders(method: String, path: String): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(method, path).withHeaders(validHeaders: _*)
}
