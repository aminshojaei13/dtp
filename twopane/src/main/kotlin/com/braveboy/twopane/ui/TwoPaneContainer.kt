package com.braveboy.twopane.ui

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.braveboy.twopane.model.TwoPaneNavNode

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun <T : TwoPaneNavNode> TwoPaneContainer(
    title: String,
    mainRows: List<T>,
    focusedIndex: Int,
    previewNode: T?,
    onUp: () -> Unit,
    onDown: () -> Unit,
    onForward: () -> Unit,
    onRight: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    mainPaneWeight: Float = 0.58f,
    previewPaneWeight: Float = 0.42f,
    darkBgColor: Color = Color(0xFF0F172A),
    panelBgColor: Color = Color(0xFF1E293B),
    selectedBgColor: Color = Color(0xFF334155),
    focusBorderColor: Color = Color(0xFF38BDF8),
    primaryTextColor: Color = Color(0xFFF8FAFC),
    mutedTextColor: Color = Color(0xFF94A3B8)
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(mainRows) {
        if (mainRows.isNotEmpty()) {
            focusRequester.requestFocus()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(darkBgColor)
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                when (event.nativeKeyEvent.keyCode) {
                    android.view.KeyEvent.KEYCODE_DPAD_UP -> { onUp(); true }
                    android.view.KeyEvent.KEYCODE_DPAD_DOWN -> { onDown(); true }
                    android.view.KeyEvent.KEYCODE_DPAD_LEFT,
                    android.view.KeyEvent.KEYCODE_DPAD_CENTER,
                    android.view.KeyEvent.KEYCODE_ENTER -> { onForward(); true }
                    android.view.KeyEvent.KEYCODE_DPAD_RIGHT -> { onRight(); true }
                    android.view.KeyEvent.KEYCODE_BACK -> { onBack(); true }
                    else -> false
                }
            }
            .padding(34.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(mainPaneWeight)
                    .fillMaxHeight()
            ) {
                Text(
                    text = title,
                    color = primaryTextColor,
                    fontSize = 30.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(mainRows, key = { _, row -> row.id }) { index, row ->
                        val selected = index == focusedIndex
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(
                                    if (selected) Modifier.border(
                                        2.dp,
                                        focusBorderColor,
                                        RoundedCornerShape(10.dp)
                                    )
                                    else Modifier
                                )
                                .background(
                                    if (selected) selectedBgColor else panelBgColor,
                                    RoundedCornerShape(10.dp)
                                )
                                .focusRequester(if (index == 0) focusRequester else remember(row.id) { FocusRequester() })
                                .focusable()
                                .padding(horizontal = 20.dp, vertical = 17.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(row.title, color = primaryTextColor, fontSize = 20.sp)
                                if (row.summary.isNotBlank()) {
                                    Text(row.summary, color = mutedTextColor, fontSize = 13.sp)
                                }
                            }
                            if (!row.isLeaf) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = primaryTextColor
                                )
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(previewPaneWeight)
                    .fillMaxHeight()
                    .background(panelBgColor, RoundedCornerShape(16.dp))
                    .padding(28.dp)
            ) {
                Text(previewNode?.title ?: "Select an item", color = primaryTextColor, fontSize = 26.sp)
                Spacer(Modifier.height(12.dp))
                Text(previewNode?.summary.orEmpty(), color = mutedTextColor, fontSize = 17.sp)
                Spacer(Modifier.height(26.dp))
                previewNode?.value?.let { Text(it, color = focusBorderColor, fontSize = 18.sp) }
                if (previewNode != null && previewNode.children.isNotEmpty()) {
                    Spacer(Modifier.height(22.dp))
                    Text(
                        "Contains ${previewNode.children.size} options",
                        color = mutedTextColor,
                        fontSize = 15.sp
                    )
                    previewNode.children.take(5).forEach { child ->
                        Spacer(Modifier.height(10.dp))
                        Text("•  ${child.title}", color = primaryTextColor, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
