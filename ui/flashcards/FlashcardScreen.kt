package com.example.vocabmaster.ui.flashcards

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vocabmaster.data.Word
import com.example.vocabmaster.viewmodel.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardScreen(
    viewModel: WordViewModel,
    navController: NavController
) {
    var showOnlyUnmemorized by remember { mutableStateOf(false) }
    val allWords by viewModel.allWords.collectAsState(initial = emptyList())

    val words = remember(allWords, showOnlyUnmemorized) {
        val filtered = if (showOnlyUnmemorized) allWords.filter { !it.isMemorized } else allWords
        filtered.sortedBy { it.isMemorized }
    }

    var currentIndex by remember { mutableStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var rotation by remember { mutableStateOf(0f) }
    val animateRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = tween(durationMillis = 400),
        label = "rotation"
    )

    val density = LocalDensity.current.density

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Флеш-карточки") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = showOnlyUnmemorized,
                    onCheckedChange = {
                        showOnlyUnmemorized = it
                        currentIndex = 0
                        isFlipped = false
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Только невыученные")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (words.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Нет слов для отображения")
                }
            } else {
                val currentWord = words[currentIndex % words.size]

                Text("${currentIndex + 1} из ${words.size}", modifier = Modifier.padding(bottom = 16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            rotation += 180f
                            isFlipped = !isFlipped
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val isFront = (animateRotation % 360f) < 90f || (animateRotation % 360f) > 270f
                    val scaleX = if (isFront) 1f else -1f

                    Text(
                        text = if (isFlipped) currentWord.english else currentWord.russian,
                        fontSize = 28.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .graphicsLayer {
                                this.rotationY = animateRotation
                                this.cameraDistance = 8 * density
                            }
                            .scale(scaleX, 1f)
                            .padding(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            viewModel.markAsNotMemorized(currentWord)
                            currentIndex++
                            isFlipped = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Не запомнил")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Не запомнил")
                    }

                    Button(
                        onClick = {
                            viewModel.markAsMemorized(currentWord)
                            currentIndex++
                            isFlipped = false
                        }
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Запомнил")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Запомнил")
                    }
                }
            }
        }
    }
}
