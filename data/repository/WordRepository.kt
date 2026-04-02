package com.example.vocabmaster.repository

import android.util.Log
import com.example.vocabmaster.api.RetrofitInstance
import com.example.vocabmaster.api.TranslateRequest
import com.example.vocabmaster.data.Word
import com.example.vocabmaster.data.WordDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.*

class WordRepository(private val wordDao: WordDao) {

    val allWords: Flow<List<Word>> = wordDao.getAllWords()
    val unmemorizedWords: Flow<List<Word>> = wordDao.getUnmemorizedWords()

    suspend fun translateText(text: String, fromLang: String, toLang: String): String? = withContext(Dispatchers.IO) {
        try {
            val request = TranslateRequest(
                sourceLanguageCode = fromLang,
                targetLanguageCode = toLang,
                texts = listOf(text),
                folderId = RetrofitInstance.folderId()
            )

            val response = RetrofitInstance.service.translate(request)
            return@withContext response.translations.firstOrNull()?.text
        } catch (e: Exception) {
            Log.e("WordRepository", "Translation failed", e)
            null
        }
    }

    suspend fun addWord(inputText: String, fromLang: String, toLang: String) = withContext(Dispatchers.IO) {
        try {
            val translated = translateText(inputText, fromLang, toLang)
            translated?.let {
                val word = if (fromLang == "ru") {
                    Word(russian = inputText, english = it)
                } else {
                    Word(russian = it, english = inputText)
                }
                wordDao.insert(word)
                Log.d("WordRepository", "Word added: $word")
            } ?: Log.e("WordRepository", "Translated text is null")
        } catch (e: Exception) {
            Log.e("WordRepository", "Failed to add word", e)
        }
    }

    suspend fun insert(word: Word) = withContext(Dispatchers.IO) {
        wordDao.insert(word)
    }

    suspend fun markAsMemorized(word: Word) = withContext(Dispatchers.IO) {
        wordDao.update(word.copy(isMemorized = true, lastReviewed = Date().time))
    }

    suspend fun markAsNotMemorized(word: Word) = withContext(Dispatchers.IO) {
        wordDao.update(word.copy(isMemorized = false, lastReviewed = Date().time))
    }

    suspend fun deleteWord(word: Word) = withContext(Dispatchers.IO) {
        wordDao.delete(word)
    }

    suspend fun setMemorized(id: String, isMemorized: Boolean) = withContext(Dispatchers.IO) {
        wordDao.updateMemorizedStatus(id, isMemorized)
    }
}
