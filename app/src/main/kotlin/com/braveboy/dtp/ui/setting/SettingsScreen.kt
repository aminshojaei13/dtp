package com.braveboy.dtp.ui.setting

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.braveboy.dtp.domain.model.SettingsNode
import com.braveboy.dtp.navigation.SettingsMain
import com.braveboy.dtp.navigation.SettingsPreview
import com.braveboy.dtp.ui.theme.DarkBackground
import com.braveboy.dtp.ui.theme.FocusBorder
import com.braveboy.dtp.ui.theme.MutedText
import com.braveboy.dtp.ui.theme.PanelBackground
import com.braveboy.dtp.ui.theme.PrimaryText
import com.braveboy.dtp.ui.theme.SelectedItemBackground
import com.example.nav3recipes.scenes.twopane.TwoPaneScene
import com.example.nav3recipes.scenes.twopane.rememberTwoPaneSceneStrategy
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

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
    val twoPaneStrategy = rememberTwoPaneSceneStrategy<NavKey>()
    val backStack = rememberNavBackStack(SettingsMain,SettingsPreview("preview"))
    val latestState by rememberUpdatedState(state)

    BackHandler(enabled = !state.isLoading) { onIntent(SettingsIntent.Back) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                when (event.nativeKeyEvent.keyCode) {
                    android.view.KeyEvent.KEYCODE_DPAD_UP -> {
                        onIntent(SettingsIntent.Up)
                        true
                    }

                    android.view.KeyEvent.KEYCODE_DPAD_DOWN -> {
                        onIntent(SettingsIntent.Down)
                        true
                    }

                    android.view.KeyEvent.KEYCODE_DPAD_LEFT,
                    android.view.KeyEvent.KEYCODE_DPAD_CENTER,
                    android.view.KeyEvent.KEYCODE_ENTER -> {
                        onIntent(SettingsIntent.Forward)
                        true
                    }

                    android.view.KeyEvent.KEYCODE_DPAD_RIGHT,
                    android.view.KeyEvent.KEYCODE_BACK -> {
                        onIntent(SettingsIntent.Back)
                        true
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
            NavDisplay(
                backStack = backStack,
                sceneStrategies = listOf(twoPaneStrategy),
                onBack = { backStack.removeLastOrNull() },
            ) { key ->
                when (key) {
                    is SettingsMain -> NavEntry(
                        key = key,
                        metadata = TwoPaneScene.twoPane()
                    ) {
                        val currentScreen = latestState.path.lastOrNull()
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = {
                                (fadeIn(animationSpec = tween(300)) + slideInHorizontally { width -> width / 4 })
                                    .togetherWith(fadeOut(animationSpec = tween(300)) + slideOutHorizontally { width -> -width / 4 })
                            },
                            label = "PageTransition"
                        ) { node ->
                            MainScreen(
                                title = node?.title ?: "Settings",
                                children = node?.children ?: latestState.mainRows,
                                onIntent = onIntent,
                                focusedIndex = latestState.focusedIndex
                            )
                        }
                    }

                    is SettingsPreview -> NavEntry(key = key, metadata = TwoPaneScene.twoPane()) {
                        AnimatedContent(
                            targetState = latestState.preview,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(250)).togetherWith(
                                    fadeOut(
                                        animationSpec = tween(250)
                                    )
                                )
                            },
                            label = "PreviewTransition"
                        ) { preview ->
                            PreviewScreen(preview = preview)
                        }
                    }

                    else -> NavEntry(key = key) {}
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    title: String,
    children: List<SettingsNode>,
    onIntent: (SettingsIntent) -> Unit,
    focusedIndex: Int = 0,
) {
    val focusRequesters = remember(children) {
        List(children.size) { FocusRequester() }
    }

    LaunchedEffect(children, focusedIndex) {
        if (children.isNotEmpty()) {
            val targetIndex = focusedIndex.coerceIn(0, children.lastIndex)
            runCatching {
                focusRequesters.getOrNull(targetIndex)?.requestFocus()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(end = 11.dp)
    ) {
        Text(
            text = title,
            color = PrimaryText,
            fontSize = 30.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(
                children,
                key = { _, item -> item.id }
            ) { index, row ->
                var isItemFocused by remember { mutableStateOf(false) }
                val isSelected = isItemFocused
                val itemFocusRequester =
                    focusRequesters.getOrElse(index) { remember(row.id) { FocusRequester() } }

                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) SelectedItemBackground else PanelBackground,
                    animationSpec = tween(durationMillis = 200),
                    label = "backgroundColor"
                )
                val borderColor by animateColorAsState(
                    targetValue = if (isSelected) FocusBorder else Color.Transparent,
                    animationSpec = tween(durationMillis = 200),
                    label = "borderColor"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            2.dp,
                            borderColor,
                            RoundedCornerShape(10.dp)
                        )
                        .background(
                            backgroundColor,
                            RoundedCornerShape(10.dp)
                        )
                        .focusRequester(itemFocusRequester)
                        .onFocusChanged { focusState ->
                            isItemFocused = focusState.isFocused
                            if (focusState.isFocused) {
                                onIntent(SettingsIntent.FocusRow(index))
                            }
                        }
                        .focusable()
                        .clickable {
                            onIntent(SettingsIntent.FocusRow(index))
                            onIntent(SettingsIntent.Forward)
                        }
                        .padding(horizontal = 20.dp, vertical = 17.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            row.title,
                            color = PrimaryText,
                            fontSize = 20.sp
                        )
                        if (row.summary.isNotBlank()) {
                            Text(
                                row.summary,
                                color = MutedText,
                                fontSize = 13.sp
                            )
                        }
                    }
                    if (row.children.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PreviewScreen(
    modifier: Modifier = Modifier,
    preview: SettingsNode?
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(PanelBackground, RoundedCornerShape(16.dp))
            .padding(28.dp)
    ) {
        Text(
            preview?.title ?: "Choose an item",
            color = PrimaryText,
            fontSize = 26.sp
        )

        Spacer(Modifier.height(12.dp))

        if (!preview?.summary.isNullOrBlank()) {
            Text(
                preview.summary,
                color = MutedText,
                fontSize = 17.sp
            )
            Spacer(Modifier.height(26.dp))
        }

        preview?.value?.let {
            Text(
                it,
                color = FocusBorder,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(22.dp))
        }

        if (preview != null && preview.children.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))

            preview.children.forEach { child ->
                Spacer(Modifier.height(10.dp))

                Text(
                    "•  ${child.title}",
                    color = PrimaryText,
                    fontSize = 16.sp
                )
            }
        }
    }
}
