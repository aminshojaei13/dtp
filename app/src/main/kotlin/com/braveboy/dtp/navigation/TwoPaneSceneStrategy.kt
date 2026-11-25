package com.braveboy.dtp.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.contains
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND

/**
 * Width ratios of the two panes. The first pane (Main) is laid out on the right and the
 * second pane (Preview) on the left, because the layout direction is RTL.
 */
data class TwoPaneLayout(
    val mainWeight: Float = DEFAULT_MAIN_WEIGHT,
    val previewWeight: Float = DEFAULT_PREVIEW_WEIGHT,
) {
    init {
        require(mainWeight > 0f && previewWeight > 0f) { "Pane weights must be positive" }
    }

    companion object {
        const val DEFAULT_MAIN_WEIGHT = 0.58f
        const val DEFAULT_PREVIEW_WEIGHT = 0.42f
    }
}

/**
 * A [Scene] that displays the last two [NavEntry]s side by side.
 */
data class TwoPaneScene<T : Any>(
    override val key: Any,
    override val previousEntries: List<NavEntry<T>>,
    val firstEntry: NavEntry<T>,
    val secondEntry: NavEntry<T>,
    val layout: TwoPaneLayout = TwoPaneLayout(),
) : Scene<T> {
    override val entries: List<NavEntry<T>> = listOf(firstEntry, secondEntry)
    override val content: @Composable (() -> Unit) = {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.weight(layout.mainWeight)) {
                firstEntry.Content()
            }
            Column(modifier = Modifier.weight(layout.previewWeight)) {
                secondEntry.Content()
            }
        }
    }

    companion object {
        /** Marks a [NavEntry] as displayable in a two-pane layout. */
        fun twoPane() = metadata {
            put(TwoPaneKey, true)
        }
    }

    object TwoPaneKey : NavMetadataKey<Boolean>
}

@Composable
fun <T : Any> rememberTwoPaneSceneStrategy(
    layout: TwoPaneLayout = TwoPaneLayout(),
): TwoPaneSceneStrategy<T> {
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass

    return remember(windowSizeClass, layout) {
        TwoPaneSceneStrategy(windowSizeClass, layout)
    }
}

/**
 * Activates a [TwoPaneScene] when the window is wide enough and the top two entries
 * both declare [TwoPaneScene.twoPane] metadata.
 */
class TwoPaneSceneStrategy<T : Any>(
    val windowSizeClass: WindowSizeClass,
    val layout: TwoPaneLayout = TwoPaneLayout(),
) : SceneStrategy<T> {

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        if (!windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            return null
        }

        val lastTwoEntries = entries.takeLast(2)
        if (lastTwoEntries.size != 2 ||
            !lastTwoEntries.all { it.metadata.contains(TwoPaneScene.TwoPaneKey) }
        ) {
            return null
        }

        val firstEntry = lastTwoEntries.first()
        val secondEntry = lastTwoEntries.last()

        return TwoPaneScene(
            key = Pair(firstEntry.contentKey, secondEntry.contentKey),
            // Back removes only the top entry, because the app pushes one entry at a time.
            previousEntries = entries.dropLast(1),
            firstEntry = firstEntry,
            secondEntry = secondEntry,
            layout = layout,
        )
    }
}
