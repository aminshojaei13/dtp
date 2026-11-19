package com.braveboy.dtp.ui.setting

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.braveboy.dtp.ui.theme.DarkBackground
import com.braveboy.dtp.ui.theme.FocusBorder
import com.braveboy.dtp.ui.theme.MutedText
import com.braveboy.dtp.ui.theme.PanelBackground
import com.braveboy.dtp.ui.theme.PrimaryText
import com.braveboy.dtp.ui.theme.SelectedItemBackground

private const val MAIN_PANE_WEIGHT = 0.58f
private const val PREVIEW_PANE_WEIGHT = 0.42f

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }

    BackHandler(enabled = !state.isLoading) { viewModel.onIntent(SettingsIntent.Back) }

    LaunchedEffect(state.mainRows) {
        if (state.mainRows.isNotEmpty()) {
            focusRequester.requestFocus()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                when (event.nativeKeyEvent.keyCode) {
                    android.view.KeyEvent.KEYCODE_DPAD_UP -> {
                        viewModel.onIntent(SettingsIntent.Up); true
                    }

                    android.view.KeyEvent.KEYCODE_DPAD_DOWN -> {
                        viewModel.onIntent(SettingsIntent.Down); true
                    }

                    android.view.KeyEvent.KEYCODE_DPAD_LEFT,
                    android.view.KeyEvent.KEYCODE_DPAD_CENTER,
                    android.view.KeyEvent.KEYCODE_ENTER -> {
                        viewModel.onIntent(SettingsIntent.Forward); true
                    }

                    android.view.KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        viewModel.onIntent(SettingsIntent.Right); true
                    }

                    android.view.KeyEvent.KEYCODE_BACK -> {
                        viewModel.onIntent(SettingsIntent.Back); true
                    }

                    else -> false
                }
            }
            .padding(34.dp)
    ) {
        if (state.isLoading) {
            Text(
                text = "Loading settings…",
                color = PrimaryText,
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (state.error != null) {
            Text(
                text = "Unable to load settings: ${state.error}",
                color = PrimaryText,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(MAIN_PANE_WEIGHT)
                        .fillMaxHeight()
                ) {
                    Text(
                        text = state.path.lastOrNull()?.title ?: "Settings",
                        color = PrimaryText,
                        fontSize = 30.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        itemsIndexed(state.mainRows, key = { _, row -> row.id }) { index, row ->
                            val selected = index == state.focusedIndex
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .then(
                                        if (selected) Modifier.border(
                                            2.dp,
                                            FocusBorder,
                                            RoundedCornerShape(10.dp)
                                        )
                                        else Modifier
                                    )
                                    .background(
                                        if (selected) SelectedItemBackground else PanelBackground,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .focusRequester(if (index == 0) focusRequester else remember(row.id) { FocusRequester() })
                                    .focusable()
                                    .padding(horizontal = 20.dp, vertical = 17.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(row.title, color = PrimaryText, fontSize = 20.sp)
                                    if (row.summary.isNotBlank()) {
                                        Text(row.summary, color = MutedText, fontSize = 13.sp)
                                    }
                                }
                                if (row.children.isEmpty().not()) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(PREVIEW_PANE_WEIGHT)
                        .fillMaxHeight()
                        .background(PanelBackground, RoundedCornerShape(16.dp))
                        .padding(28.dp)
                ) {
                    val preview = state.preview

                    Text(preview?.title ?: "Choose an item", color = PrimaryText, fontSize = 26.sp)

                    Spacer(Modifier.height(12.dp))

                    Text(preview?.summary.orEmpty(), color = MutedText, fontSize = 17.sp)

                    Spacer(Modifier.height(26.dp))

                    preview?.value?.let { Text(it, color = FocusBorder, fontSize = 18.sp) }
                    if (preview != null && preview.children.isNotEmpty()) {
                        Spacer(Modifier.height(22.dp))

                        Text(
                            "Contains ${preview.children.size} options",
                            color = MutedText,
                            fontSize = 15.sp
                        )

                        preview.children.take(5).forEach {
                            Spacer(Modifier.height(10.dp))

                            Text("•  ${it.title}", color = PrimaryText, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
