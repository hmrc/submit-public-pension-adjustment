/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package repository

import com.google.inject.Inject
import play.api.libs.json.{JsValue, Json, OWrites}
import uk.gov.hmrc.crypto.{Crypted, Decrypter, Encrypter, PlainText, SymmetricCryptoFactory}
import uk.gov.hmrc.selfassessmentrefundbackend.config.AppConfig

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

