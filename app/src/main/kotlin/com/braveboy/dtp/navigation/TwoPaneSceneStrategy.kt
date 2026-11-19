package com.braveboy.dtp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope

@Composable
fun <T : Any> rememberTwoPaneSceneStrategy(): TwoPaneSceneStrategy<T> = remember {
    TwoPaneSceneStrategy()
}

class TwoPaneSceneStrategy<T : Any> : SceneStrategy<T> {
    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        if (entries.isEmpty()) return null
        val previous = entries.dropLast(1)
        return TwoPaneScene(
            key = entries.last().contentKey,
            entries = entries,
            previousEntries = previous
        )
    }
}

data class TwoPaneScene<T : Any>(
    override val key: Any,
    override val entries: List<NavEntry<T>>,
    override val previousEntries: List<NavEntry<T>>
) : Scene<T> {
    override val content: @Composable () -> Unit = {
        if (entries.size == 1) {
            entries.first().Content()
        } else {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(0.4f).fillMaxHeight()) {
                    entries.first().Content()
                }
                Box(modifier = Modifier.weight(0.6f).fillMaxHeight()) {
                    entries.last().Content()
                }
            }
        }
    }
}