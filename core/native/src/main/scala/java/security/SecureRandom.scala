package java.security

import scala.util.Random

/** A stub implementation of SecureRandom for Scala Native.
  *
  * Note: This uses scala.util.Random which is NOT cryptographically secure. This is only suitable
  * for non-security-critical UUID generation.
  */
class SecureRandom extends Random {
  override def nextBytes(bytes: Array[Byte]): Unit = {
    var i = 0
    while (i < bytes.length) {
      bytes(i) = nextInt(256).toByte
      i += 1
    }
  }
}
