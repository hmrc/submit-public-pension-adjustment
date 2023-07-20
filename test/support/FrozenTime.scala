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

package support

import java.time._

/**
 * A time machine which allows to travel back and forth in time.
 */

object FrozenTime {

  def setTime(fixedAtDate: LocalDate): Unit = {
    val clock = DatesSupport.fixedClockUTC(fixedAtDate)
    currentClock = clock
  }

  def setTime(fixedAtDateTime: LocalDateTime): Unit = {
    val clock = DatesSupport.fixedClockUTC(fixedAtDateTime)
    currentClock = clock
  }

  def addSeconds(seconds: Long): Unit = {
    val nowPlusSeconds = LocalDateTime.now(testClock).plusSeconds(seconds)
    setTime(nowPlusSeconds)
  }

  def addHours(hours: Long): Unit = {
    setTime(LocalDateTime.now(testClock).plusHours(hours))
  }

  def setTime(fixedAtDate: String): Unit = {
    val clock = DatesSupport.fixedClockUTC(LocalDate.parse(fixedAtDate))
    currentClock = clock
  }

  def instant: Instant = Instant.now(testClock)
  def localDateTime: LocalDateTime = LocalDateTime.now(testClock)
  def localDate: LocalDate = LocalDate.now(testClock)
  def getClock: Clock = testClock

  def reset(): Unit = setTime(initialLocalDate)

  private val initialLocalDate = LocalDate.parse("2020-12-25")
  private var currentClock: Clock = DatesSupport.fixedClockUTC(initialLocalDate)
  private val testClock: Clock = new Clock {
    override def getZone(): ZoneId = currentClock.getZone
    override def withZone(zoneId: ZoneId): Clock = currentClock.withZone(zoneId)
    override def instant(): Instant = currentClock.instant()
  }
}
