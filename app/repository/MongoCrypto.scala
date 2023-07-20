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

package repository

import com.google.inject.Inject
import play.api.libs.json.{JsValue, Json, OWrites}
import uk.gov.hmrc.crypto.{Crypted, Decrypter, Encrypter, PlainText, SymmetricCryptoFactory}
import uk.gov.hmrc.submitpublicpensionadjustment.config.AppConfig

import javax.inject.Singleton

trait MongoCrypto {
  def encryptStr[A](s: A): String
  def decryptStr[A](s: A): String

  def encryptJson[A](s: A)(implicit write: OWrites[A]): String
  def decryptJson[A](s: A): JsValue
}

@Singleton
class MongoCryptoImpl @Inject()(appConfig: AppConfig) extends MongoCrypto {

  private val aesCryptoKey: String = appConfig.encryptionKey.trim

  // Use SymmetricCryptoFactory.aesCrypto (not aesGcmCrypto) as we need repeatable encryptions
  private val aesCrypto: Encrypter with Decrypter = SymmetricCryptoFactory.aesCrypto(aesCryptoKey)

  def encryptStr[A](s: A): String = aesCrypto.encrypt(PlainText(s.toString)).value
  def decryptStr[A](s: A): String = aesCrypto.decrypt(Crypted(s.toString)).value

  def encryptJson[A](s: A)(implicit write: OWrites[A]): String = aesCrypto.encrypt(PlainText(Json.toJson(s).toString)).value
  def decryptJson[A](s: A): JsValue = Json.parse(aesCrypto.decrypt(Crypted(s.toString)).value)
}

