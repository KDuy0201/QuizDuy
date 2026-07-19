package com.example.quizduy.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

fun Modifier.bouncyOverscroll(): Modifier = composed {
    val translationY = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (translationY.value != 0f) {
                    // Consume scroll to reduce bounce first
                    val consumed = if (translationY.value > 0) {
                        if (available.y < 0) available.y else 0f
                    } else {
                        if (available.y > 0) available.y else 0f
                    }
                    
                    coroutineScope.launch {
                        translationY.snapTo(translationY.value + consumed * 0.5f)
                    }
                    return Offset(0f, consumed)
                }
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // If there's available scroll left, it means we hit the edge. 
                // Add it to our translation.
                if (available.y != 0f) {
                    coroutineScope.launch {
                        translationY.snapTo(translationY.value + available.y * 0.3f)
                    }
                }
                return Offset.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                // Bounce back when finger is lifted
                coroutineScope.launch {
                    translationY.animateTo(0f, spring(stiffness = 200f, dampingRatio = 0.6f))
                }
                return Velocity.Zero
            }
        }
    }

    this
        .nestedScroll(nestedScrollConnection)
        .offset { IntOffset(0, translationY.value.roundToInt()) }
}
