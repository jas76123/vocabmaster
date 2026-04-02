package com.example.vocabmaster.api

import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.MGF1ParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.PSSParameterSpec
import java.util.*

/**
 * Учётные данные сервисного аккаунта Yandex Cloud.
 *
 * ВАЖНО: это секреты — их НЕЛЬЗЯ хранить в системе контроля версий.
 * Реальные значения задаются локально (например, через local.properties /
 * BuildConfig или переменные окружения) и не коммитятся в репозиторий.
 * Ниже — только заглушки-плейсхолдеры, чтобы код собирался как образец.
 *
 * Получить свои значения: Yandex Cloud → сервисный аккаунт → авторизованный
 * ключ (даёт service account id, key id и приватный ключ).
 */
object IamTokenProvider {
    private const val SERVICE_ACCOUNT_ID = "YOUR_SERVICE_ACCOUNT_ID"
    private const val KEY_ID = "YOUR_KEY_ID"
    private const val PRIVATE_KEY = """
        -----BEGIN PRIVATE KEY-----
        YOUR_PRIVATE_KEY_HERE
        -----END PRIVATE KEY-----
    """

    private const val IAM_URL = "https://iam.api.cloud.yandex.net/iam/v1/tokens"

    suspend fun getIamToken(): String? = withContext(Dispatchers.IO) {
        try {
            val jwt = createJwt()
            val json = """
                {
                  "jwt": "$jwt"
                }
            """
            val request = Request.Builder()
                .url(IAM_URL)
                .post(json.toRequestBody("application/json".toMediaType()))
                .build()

            val response = OkHttpClient().newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody == null) return@withContext null
            return@withContext JSONObject(responseBody).getString("iamToken")

        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    private fun createJwt(): String {
        val now = System.currentTimeMillis() / 1000
        val header = JSONObject(mapOf(
            "alg" to "PS256",
            "typ" to "JWT",
            "kid" to KEY_ID
        )).toString()

        val payload = JSONObject(mapOf(
            "aud" to IAM_URL,
            "iss" to SERVICE_ACCOUNT_ID,
            "iat" to now,
            "exp" to now + 360
        )).toString()

        val headerEncoded = Base64.encodeToString(header.toByteArray(), Base64.URL_SAFE or Base64.NO_WRAP)
        val payloadEncoded = Base64.encodeToString(payload.toByteArray(), Base64.URL_SAFE or Base64.NO_WRAP)
        val message = "$headerEncoded.$payloadEncoded"

        val signature = signJwt(message)
        return "$message.$signature"
    }

    private fun signJwt(message: String): String {
        val privateKeyPem = PRIVATE_KEY.replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\n", "")
        val keySpec = PKCS8EncodedKeySpec(Base64.decode(privateKeyPem, Base64.DEFAULT))
        val privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec)

        val signature = Signature.getInstance("SHA256withRSA/PSS").apply {
            setParameter(PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1))
            initSign(privateKey)
            update(message.toByteArray())
        }.sign()

        return Base64.encodeToString(signature, Base64.URL_SAFE or Base64.NO_WRAP)
    }
}
