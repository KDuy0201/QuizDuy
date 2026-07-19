package com.example.quizduy.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quizduy.data.Deck
import com.example.quizduy.data.DeckRepository
import com.example.quizduy.data.Flashcard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class QuizViewModel(private val repository: DeckRepository) : ViewModel() {

    private val _decks = MutableStateFlow<List<Deck>>(emptyList())
    val decks: StateFlow<List<Deck>> = _decks.asStateFlow()

    init {
        loadDecks()
    }

    private fun loadDecks() {
        _decks.value = repository.getDecks()
    }

    fun saveDeck(deck: Deck) {
        val validCards = deck.cards.filter { it.term.isNotBlank() || it.desire.isNotBlank() }
        val finalDeck = deck.copy(cards = validCards)
        
        if (finalDeck.name.isNotBlank() || finalDeck.cards.isNotEmpty()) {
            repository.saveOrUpdateDeck(finalDeck)
        } else if (finalDeck.name.isBlank() && finalDeck.cards.isEmpty()) {
            // Delete if everything is empty
            repository.deleteDeck(deck.id)
        }
        loadDecks()
    }

    fun addCardToDeck(deckId: String, term: String, desire: String) {
        if (term.isNotBlank() && desire.isNotBlank()) {
            val newCard = Flashcard(term = term, desire = desire)
            repository.addCardToDeck(deckId, newCard)
            loadDecks()
        }
    }

    fun getDeckById(deckId: String): Deck? {
        return _decks.value.find { it.id == deckId }
    }
}

class QuizViewModelFactory(private val repository: DeckRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuizViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
