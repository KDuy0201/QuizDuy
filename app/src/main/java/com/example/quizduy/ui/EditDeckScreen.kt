package com.example.quizduy.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizduy.data.Deck
import com.example.quizduy.data.Flashcard
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDeckScreen(
    deckId: String,
    viewModel: QuizViewModel,
    onNavigateBack: () -> Unit
) {
    // If deckId == "new", we are creating a new deck. Otherwise, we load existing.
    val isNewDeck = deckId == "new"
    val existingDeck = if (!isNewDeck) viewModel.getDeckById(deckId) else null

    val deckName = remember { mutableStateOf(existingDeck?.name ?: "") }
    
    // We use a SnapshotStateList so UI updates when items are added/modified
    val cards = remember { 
        mutableStateListOf<Flashcard>().apply {
            if (existingDeck != null) {
                addAll(existingDeck.cards)
            } else {
                // Add 2 empty cards by default for a new deck
                add(Flashcard(term = "", desire = ""))
                add(Flashcard(term = "", desire = ""))
            }
        }
    }

    // Function to save and exit
    val saveAndExit = {
        val finalDeck = Deck(
            id = existingDeck?.id ?: UUID.randomUUID().toString(),
            name = deckName.value,
            cards = cards.toList()
        )
        viewModel.saveDeck(finalDeck)
        onNavigateBack()
    }

    // Intercept back button press
    BackHandler {
        saveAndExit()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isNewDeck) "Tạo học phần" else "Sửa học phần", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { saveAndExit() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { saveAndExit() }) {
                        Icon(Icons.Filled.Check, contentDescription = "Save", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    cards.add(Flashcard(term = "", desire = ""))
                },
                containerColor = Color(0xFF4C6FFF), // A nice blue similar to Quizlet
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Card")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).imePadding().padding(horizontal = 16.dp)) {
            
            OutlinedTextField(
                value = deckName.value,
                onValueChange = { deckName.value = it },
                label = { Text("Tiêu đề") },
                placeholder = { Text("Chủ đề, chương, đơn vị") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4C6FFF),
                    focusedLabelColor = Color(0xFF4C6FFF)
                )
            )

            LazyColumn(modifier = Modifier.fillMaxSize().bouncyOverscroll()) {
                itemsIndexed(cards) { index, card ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            
                            TextField(
                                value = card.term,
                                onValueChange = { newTerm ->
                                    cards[index] = card.copy(term = newTerm)
                                },
                                label = { Text("THUẬT NGỮ") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Black,
                                    unfocusedIndicatorColor = Color.Gray
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            TextField(
                                value = card.desire,
                                onValueChange = { newDesire ->
                                    cards[index] = card.copy(desire = newDesire)
                                },
                                label = { Text("ĐỊNH NGHĨA") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Black,
                                    unfocusedIndicatorColor = Color.Gray
                                )
                            )
                        }
                    }
                }
                
                // Add some space at the bottom for the FAB
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}
