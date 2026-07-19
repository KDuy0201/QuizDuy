package com.example.quizduy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quizduy.data.DeckRepository
import com.example.quizduy.ui.EditDeckScreen
import com.example.quizduy.ui.DeckScreen
import com.example.quizduy.ui.HomeScreen
import com.example.quizduy.ui.LearnScreen
import com.example.quizduy.ui.QuizViewModel
import com.example.quizduy.ui.QuizViewModelFactory
import com.example.quizduy.ui.TestScreen
import com.example.quizduy.ui.theme.QuizDuyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val repository = DeckRepository(applicationContext)

        setContent {
            QuizDuyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QuizApp(repository)
                }
            }
        }
    }
}

@Composable
fun QuizApp(repository: DeckRepository) {
    val navController = rememberNavController()
    val viewModel: QuizViewModel = viewModel(
        factory = QuizViewModelFactory(repository)
    )

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onDeckClick = { deckId ->
                    navController.navigate("deck/$deckId")
                },
                onCreateDeckClick = {
                    navController.navigate("editDeck/new")
                }
            )
        }
        composable(
            route = "deck/{deckId}",
            arguments = listOf(navArgument("deckId") { type = NavType.StringType })
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getString("deckId") ?: return@composable
            DeckScreen(
                deckId = deckId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onEditClick = { navController.navigate("editDeck/$deckId") },
                onLearnClick = { navController.navigate("learn/$deckId") },
                onTestClick = { navController.navigate("test/$deckId") }
            )
        }
        composable(
            route = "editDeck/{deckId}",
            arguments = listOf(navArgument("deckId") { type = NavType.StringType })
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getString("deckId") ?: return@composable
            EditDeckScreen(
                deckId = deckId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "learn/{deckId}",
            arguments = listOf(navArgument("deckId") { type = NavType.StringType })
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getString("deckId") ?: return@composable
            LearnScreen(
                deckId = deckId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "test/{deckId}",
            arguments = listOf(navArgument("deckId") { type = NavType.StringType })
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getString("deckId") ?: return@composable
            TestScreen(
                deckId = deckId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}