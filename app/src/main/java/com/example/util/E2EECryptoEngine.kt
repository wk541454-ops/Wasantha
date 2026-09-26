package com.example.util

import android.util.Base64
import android.util.Log
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object E2EECryptoEngine {

    private const val TAG = "E2EECryptoEngine"
    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val GCM_TAG_LENGTH_BITS = 128
    private const val GCM_IV_LENGTH_BYTES = 12
    private const val PREFIX = "enc_v1:"

    private val secureRandom = SecureRandom()

    /**
     * Derives a 256-bit AES SecretKey deterministically from a conversation or shared key identifier.
     */
    private fun deriveKey(conversationId: String, masterSalt: String = "FriendHub_E2EE_Master_Salt_2026"): SecretKey {
        val digest = MessageDigest.getInstance("SHA-256")
        val combined = "$conversationId:$masterSalt".toByteArray(Charsets.UTF_8)
        val keyBytes = digest.digest(combined)
        return SecretKeySpec(keyBytes, "AES")
    }

    /**
     * Encrypts a plaintext message payload using AES-256-GCM.
     * Returns Base64 encoded payload prefixed with "enc_v1:"
     */
    fun encryptMessage(plainText: String, conversationId: String): String {
        if (plainText.isBlank()) return plainText
        if (plainText.startsWith(PREFIX)) return plainText // Already encrypted

        return try {
            val key = deriveKey(conversationId)
            val iv = ByteArray(GCM_IV_LENGTH_BYTES)
            secureRandom.nextBytes(iv)

            val cipher = Cipher.getInstance(ALGORITHM)
            val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec)

            val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

            val byteBuffer = ByteBuffer.allocate(iv.size + encryptedBytes.size)
            byteBuffer.put(iv)
            byteBuffer.put(encryptedBytes)

            val base64 = Base64.encodeToString(byteBuffer.array(), Base64.NO_WRAP)
            "$PREFIX$base64"
        } catch (e: Exception) {
            Log.e(TAG, "Encryption failed: ${e.message}")
            plainText
        }
    }

    /**
     * Decrypts an AES-256-GCM encrypted message payload.
     * If the payload is unencrypted, returns it safely as-is.
     */
    fun decryptMessage(cipherPayload: String, conversationId: String): String {
        if (!cipherPayload.startsWith(PREFIX)) {
            return cipherPayload // Unencrypted fallback
        }

        return try {
            val base64Data = cipherPayload.removePrefix(PREFIX)
            val rawBytes = Base64.decode(base64Data, Base64.NO_WRAP)

            if (rawBytes.size < GCM_IV_LENGTH_BYTES + 16) {
                return cipherPayload
            }

            val byteBuffer = ByteBuffer.wrap(rawBytes)
            val iv = ByteArray(GCM_IV_LENGTH_BYTES)
            byteBuffer.get(iv)

            val encryptedBytes = ByteArray(byteBuffer.remaining())
            byteBuffer.get(encryptedBytes)

            val key = deriveKey(conversationId)
            val cipher = Cipher.getInstance(ALGORITHM)
            val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec)

            val decryptedBytes = cipher.doFinal(encryptedBytes)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            Log.e(TAG, "Decryption failed: ${e.message}")
            "🔒 [Encrypted Message]"
        }
    }
}
