package com.example.quizduy.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizduy.data.Flashcard
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreen(deckId: String, viewModel: QuizViewModel, onNavigateBack: () -> Unit) {
    val deck = viewModel.getDeckById(deckId)
    if (deck == null || deck.cards.isEmpty()) {
        onNavigateBack()
        return
    }

    // Initialize test session
    var testCards by remember {
        mutableStateOf(deck.cards.shuffled().take(5))
    }
    
    // true = ask desire, false = ask term
    var askDesireList by remember {
        mutableStateOf(testCards.map { Random.nextBoolean() })
    }

    var currentIndex by remember { mutableStateOf(0) }
    var answer by remember { mutableStateOf("") }
    var score by remember { mutableStateOf(0) }
    var showResult by remember { mutableStateOf(false) }
    var showFeedback by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Test: ${deck.name}") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showResult) {
                Text("Test Complete!", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Your Score: $score / ${testCards.size}", fontSize = 24.sp)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = onNavigateBack) {
                    Text("Finish")
                }
            } else {
                Text("Question ${currentIndex + 1} of ${testCards.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(32.dp))

                val currentCard = testCards[currentIndex]
                val askDesire = askDesireList[currentIndex]
                
                val questionText = if (askDesire) currentCard.term else currentCard.desire
                val expectedAnswer = if (askDesire) currentCard.desire else currentCard.term

                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("What is the ${if(askDesire) "Desire" else "Term"} for:", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(questionText, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = answer,
                    onValueChange = { answer = it },
                    label = { Text("Your Answer") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (showFeedback) {
                    Text(
                        text = if (isCorrect) "Correct!" else "Incorrect! The answer is: $expectedAnswer",
                        color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        showFeedback = false
                        answer = ""
                        if (currentIndex < testCards.size - 1) {
                            currentIndex++
                        } else {
                            showResult = true
                        }
                    }) {
                        Text("Next")
                    }
                } else {
                    Button(
                        onClick = {
                            isCorrect = answer.trim().equals(expectedAnswer.trim(), ignoreCase = true)
                            if (isCorrect) score++
                            showFeedback = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = answer.isNotBlank()
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
    }
}
