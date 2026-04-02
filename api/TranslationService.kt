package com.example.vocabmaster.api

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class TranslateRequest(
    val sourceLanguageCode: String,
    val targetLanguageCode: String,
    val texts: List<String>,
    val folderId: String
)

data class Translation(val text: String)
data class TranslateResponse(val translations: List<Translation>)

interface TranslationService {
    @POST("translate/v2/translate")
    suspend fun translate(@Body request: TranslateRequest): TranslateResponse
}