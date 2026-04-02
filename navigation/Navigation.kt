package com.example.vocabmaster.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vocabmaster.ui.dictionary.DictionaryScreen
import com.example.vocabmaster.ui.flashcards.FlashcardScreen
import com.example.vocabmaster.viewmodel.WordViewModel

@Composable
fun VocabMasterNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "dictionary"
    ) {
        composable("dictionary") { backStackEntry ->
            val viewModel: WordViewModel = hiltViewModel(backStackEntry)
            DictionaryScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
        composable("flashcards") { backStackEntry ->
            val viewModel: WordViewModel = hiltViewModel(backStackEntry)
            FlashcardScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}