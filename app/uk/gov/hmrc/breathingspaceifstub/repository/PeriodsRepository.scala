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

package uk.gov.hmrc.breathingspaceifstub.repository

import java.time.LocalDate
import java.util.UUID
import javax.inject.{Inject, Singleton}

import scala.collection.mutable

import cats.syntax.option._
import uk.gov.hmrc.breathingspaceifstub._
import uk.gov.hmrc.breathingspaceifstub.model.{Nino, Period}

@Singleton()
class PeriodsRepository @Inject()(debtorRepository: DebtorRepository) {

  val store: mutable.Map[Nino, Periods] = {
    mutable.Map[Nino, Periods](
      // Adding to the store one-Period-list record for each Nino in DebtorRepository.
      debtorRepository.store.keySet.toList.zipWithIndex
        .map(t => (t._1, genOnePeriodList(t._2))): _*
    )
  }

  private val monitor = new AnyRef

  def add(nino: Nino, periodsToAdd: Periods): Periods = monitor.synchronized {
    store
      .get(nino)
      .fold {
        // No Period-list record in the store for the given Nino. Adding a new record.
        store += (nino -> periodsToAdd)
        periodsToAdd
      } { periods =>
        // At least one-Period-list record exists in the repo for the given Nino.
        // Filter out any Period item with startDate and endDate already in the repo for the given Nino.
        val filteredPeriodsToAdd = filterOutPeriodsIfSameDates(periods, periodsToAdd)
        // Replacing the record with the new "all Periods" list.
        store.put(nino, periods ++ filteredPeriodsToAdd)
        filteredPeriodsToAdd
      }
  }

  def get(nino: Nino): Option[Periods] = monitor.synchronized(store.get(nino))

  private def filterOutPeriodsIfSameDates(periods: Periods, periodsToAdd: Periods): Periods =
    periodsToAdd.filter(
      periodToAdd =>
        periods.forall { period =>
          period.startDate != periodToAdd.startDate || period.endDate != periodToAdd.endDate
        }
    )

  def genOnePeriodList(ix: Int, startDate: LocalDate = LocalDate.of(2020, 1, 1)): Periods =
    List(Period(UUID.randomUUID(), startDate.plusDays(ix), startDate.plusMonths(2).some))
}
