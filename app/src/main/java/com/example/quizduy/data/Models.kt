package com.example.quizduy.data

import java.util.UUID

data class Flashcard(
    val id: String = UUID.randomUUID().toString(),
    val term: String,
    val desire: String
)

data class Deck(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val cards: List<Flashcard> = emptyList()
)
