package com.braveboy.dtp.ui.setting

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Text
import com.braveboy.dtp.ui.theme.PrimaryText
import com.braveboy.twopane.ui.TwoPaneContainer

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsScreen(
        state = state,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun SettingsScreen(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit = {}
) {
    BackHandler(enabled = !state.isLoading) { onIntent(SettingsIntent.Back) }

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Loading settings…",
                color = PrimaryText
            )
        }
    } else if (state.error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Unable to load settings: ${state.error}",
                color = PrimaryText
            )
        }
    } else {
        TwoPaneContainer(
            title = state.path.lastOrNull()?.title ?: "Settings",
            mainRows = state.mainRows,
            focusedIndex = state.focusedIndex,
            previewNode = state.preview,
            onUp = { onIntent(SettingsIntent.Up) },
            onDown = { onIntent(SettingsIntent.Down) },
            onForward = { onIntent(SettingsIntent.Forward) },
            onRight = { onIntent(SettingsIntent.Right) },
            onBack = { onIntent(SettingsIntent.Back) }
        )
    }
}
