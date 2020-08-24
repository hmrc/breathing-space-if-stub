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

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.breathingspaceifstub.model.Debtor

object DebtorRepository {

  lazy val debtors: Map[String, JsValue] = List(
    ("HT423277B", "Lawrence", "Velazquez", "1954-10-04"),
    ("AB188148D", "Luigi", "Pisani", "1935-04-20"),
    ("AB888330D", "Roos", "Tewes", "1969-06-30"),
    ("KA339738D", "Stefano", "Reese", "1974-02-27"),
    ("AB576139C", "Marni", "Dunkley", "1983-08-10"),
    ("AB807993C", "Wolfgang", "Traübe", "1983-08-26"),
    ("MZ006526C", "Ignác", "Šarlota", "1956-10-20"),
    ("BS088353B", "Maja", "Głowa", "2000-10-06"),
    ("AB445870B", "Rosalie", "Gallegos", "1987-04-08"),
    ("SJ372380A", "Nevio", "Sabina", "1956-10-09"),
    ("CB986300D", "John", "Millar", "1954-10-02"),
    ("GT948987A", "Aleksandrov", "Cherganski", "1987-09-23"),
    ("OX749001C", "Eva", "Vassilis", "1976-05-22"),
    ("LE183343C", "Yvonne", "Albin", "1947-04-06"),
    ("OT575524B", "Elsa", "Viggo", "2000-10-06")
  ).foldLeft(Map[String, JsValue]()) { (map, t) =>
    val debtor = Debtor(t._1, t._2, t._3, t._4)
    map + (debtor.nino -> Json.toJson(debtor))
  }

  def get(nino: String): Option[JsValue] = debtors.get(nino)
}
