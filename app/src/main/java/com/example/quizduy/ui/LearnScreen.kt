package com.example.quizduy.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(deckId: String, viewModel: QuizViewModel, onNavigateBack: () -> Unit) {
    val deck = viewModel.getDeckById(deckId)
    if (deck == null || deck.cards.isEmpty()) {
        onNavigateBack()
        return
    }

    var currentIndex by remember { mutableStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "flipAnimation"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Learn: ${deck.name}") },
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
            Text(
                text = "Card ${currentIndex + 1} / ${deck.cards.size}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Flashcard
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clickable { isFlipped = !isFlipped }
                    .graphicsLayer {
                        rotationY = rotation
                        cameraDistance = 8 * density
                    },
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (rotation <= 90f) {
                        // Front
                        Text(
                            text = deck.cards[currentIndex].term,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        // Back
                        Text(
                            text = deck.cards[currentIndex].desire,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(16.dp)
                                .graphicsLayer { rotationY = 180f } // Fix mirrored text
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(
                    onClick = {
                        isFlipped = false
                        if (currentIndex > 0) currentIndex--
                    },
                    enabled = currentIndex > 0
                ) {
                    Text("Previous")
                }
                Button(
                    onClick = {
                        isFlipped = false
                        if (currentIndex < deck.cards.size - 1) currentIndex++
                    },
                    enabled = currentIndex < deck.cards.size - 1
                ) {
                    Text("Next")
                }
            }
        }
    }
}
