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

package uk.gov.hmrc.breathingspaceifstub.utils

import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

import akka.stream.Materializer
import com.kenshoo.play.metrics.MetricsFilter
import org.slf4j.LoggerFactory
import play.api.http.DefaultHttpFilters
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.filters.{AuditFilter, CacheControlFilter, LoggingFilter, MDCFilter}

class Filters @Inject()(
  metricsFilter: MetricsFilter,
  auditFilter: AuditFilter,
  loggingFilter: LoggingFilter,
  cacheFilter: CacheControlFilter,
  mdcFilter: MDCFilter,
  implicit val mat: Materializer,
  implicit val ec: ExecutionContext
) extends DefaultHttpFilters(
      metricsFilter,
      auditFilter,
      loggingFilter,
      cacheFilter,
      mdcFilter,
      new StubRequestLoggingFilter()
    )

class StubRequestLoggingFilter @Inject()(implicit val mat: Materializer, ec: ExecutionContext) extends Filter {

  lazy val logger = LoggerFactory.getLogger(this.getClass)

  def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] =
    nextFilter(requestHeader).map { result =>
      logger.info(
        s"BS-STUB >> REQUEST: = ${requestHeader.method} ${requestHeader.uri} ${requestHeader.headers} << RETURNED ${result.header.status}"
      )
      result
    }
}
