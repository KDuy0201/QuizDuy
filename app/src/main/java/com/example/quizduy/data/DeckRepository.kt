package com.example.quizduy.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DeckRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("quizduy_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    private val DECKS_KEY = "decks_key"

    fun getDecks(): List<Deck> {
        val json = prefs.getString(DECKS_KEY, null)
        if (json.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<Deck>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveDecks(decks: List<Deck>) {
        val json = gson.toJson(decks)
        prefs.edit().putString(DECKS_KEY, json).apply()
    }

    fun saveOrUpdateDeck(deck: Deck) {
        val current = getDecks().toMutableList()
        val index = current.indexOfFirst { it.id == deck.id }
        if (index != -1) {
            current[index] = deck
        } else {
            current.add(0, deck)
        }
        saveDecks(current)
    }

    fun deleteDeck(deckId: String) {
        val current = getDecks().filter { it.id != deckId }
        saveDecks(current)
    }

    fun addCardToDeck(deckId: String, card: Flashcard) {
        val decks = getDecks().map { deck ->
            if (deck.id == deckId) {
                deck.copy(cards = deck.cards + card)
            } else {
                deck
            }
        }
        saveDecks(decks)
    }
}
