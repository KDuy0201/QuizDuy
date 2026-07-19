package com.example.quizduy.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckScreen(
    deckId: String,
    viewModel: QuizViewModel,
    onNavigateBack: () -> Unit,
    onEditClick: () -> Unit,
    onLearnClick: () -> Unit,
    onTestClick: () -> Unit
) {
    val deck = viewModel.getDeckById(deckId)

    if (deck == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Deck not found")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(deck.name) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = onEditClick) {
                    Text("Chỉnh sửa")
                }
                Button(onClick = onLearnClick, enabled = deck.cards.isNotEmpty()) {
                    Text("Learn")
                }
                Button(onClick = onTestClick, enabled = deck.cards.size >= 1) {
                    Text("Test")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Cards in this deck:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            if (deck.cards.isEmpty()) {
                Text("No cards yet. Add some!")
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(deck.cards) { card ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Term: ${card.term}", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Desire: ${card.desire}")
                            }
                        }
                    }
                }
            }
        }
    }
}
