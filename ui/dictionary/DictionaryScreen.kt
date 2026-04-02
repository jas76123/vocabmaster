package com.example.vocabmaster.ui.dictionary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vocabmaster.data.Word
import com.example.vocabmaster.viewmodel.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryScreen(
    viewModel: WordViewModel,
    navController: NavController
) {
    val words by viewModel.allWords.collectAsState(initial = emptyList())
    var newWord by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val isRussianToEnglish by viewModel.isRussianToEnglish.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Словарь") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("flashcards") }) {
                Icon(Icons.Default.Add, contentDescription = "Перейти к карточкам")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(
                    onClick = { viewModel.toggleTranslationDirection() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRussianToEnglish) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("RU → EN")
                }

                Button(
                    onClick = { viewModel.toggleTranslationDirection() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isRussianToEnglish) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("EN → RU")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = newWord,
                onValueChange = { newWord = it },
                label = { Text("Новое слово") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (newWord.trim().isNotEmpty()) {
                        viewModel.addWord(newWord.trim())  // <-- Теперь без fromLang и toLang
                        newWord = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Добавить и перевести")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Все слова:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (words.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Список слов пуст", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(words) { word ->
                        WordItem(word = word, onDelete = { viewModel.deleteWord(word) })
                    }
                }
            }
        }
    }
}
