package com.example.vocabmaster.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.vocabmaster.data.Word
import com.example.vocabmaster.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordViewModel @Inject constructor(
    private val repository: WordRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val allWords = repository.allWords
    val unmemorizedWords: LiveData<List<Word>> = repository.unmemorizedWords.asLiveData()

    private val _isRussianToEnglish = MutableStateFlow(true)
    val isRussianToEnglish = _isRussianToEnglish.asStateFlow()

    fun toggleTranslationDirection() {
        _isRussianToEnglish.value = !_isRussianToEnglish.value
    }

    // исправленная функция
    fun addWord(inputText: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val fromLang = if (_isRussianToEnglish.value) "ru" else "en"
            val toLang = if (_isRussianToEnglish.value) "en" else "ru"

            repository.addWord(inputText, fromLang, toLang)

            _isLoading.value = false
        }
    }

    fun markAsMemorized(word: Word) {
        viewModelScope.launch {
            repository.markAsMemorized(word)
        }
    }

    fun markAsNotMemorized(word: Word) {
        viewModelScope.launch {
            repository.markAsNotMemorized(word)
        }
    }

    fun deleteWord(word: Word) {
        viewModelScope.launch {
            repository.deleteWord(word)
        }
    }

    fun markAsMemorized(id: String, isMemorized: Boolean) {
        viewModelScope.launch {
            repository.setMemorized(id, isMemorized)
        }
    }
}
