package com.braveboy.dtp.ui.setting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.braveboy.dtp.domain.model.SettingsNode
import com.braveboy.dtp.domain.usecase.GetRootSettingsUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val getRootSettingsUseCase: GetRootSettingsUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<SettingsEffect>()
    val effects = _effects.asSharedFlow()


    init { load() }

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            SettingsIntent.Up -> moveFocus(-1)
            SettingsIntent.Down -> moveFocus(1)
            SettingsIntent.Forward -> navigateForward()
            SettingsIntent.Right -> navigateBack(exitOnRoot = false)
            SettingsIntent.Back -> navigateBack(exitOnRoot = true)
            is SettingsIntent.FocusRow -> focusRow(intent.index)
            SettingsIntent.Retry -> load()
        }
    }

    private fun moveFocus(delta: Int) {
        val currentState = _state.value
        if (currentState.mainRows.isEmpty()) return
        val newIndex = (currentState.focusedIndex + delta).coerceIn(0, currentState.mainRows.lastIndex)
        focusRow(newIndex)
    }

    private fun focusRow(index: Int) {
        _state.update { s ->
            if (s.mainRows.isEmpty()) return@update s
            val validIndex = index.coerceIn(0, s.mainRows.lastIndex)
            s.copy(
                focusedIndex = validIndex,
                preview = s.mainRows.getOrNull(validIndex)
            )
        }
    }

    private fun navigateForward() {
        val currentState = _state.value
        val focusedNode = currentState.mainRows.getOrNull(currentState.focusedIndex) ?: return
        if (focusedNode.children.isNotEmpty()) {
            val newPath = currentState.path + focusedNode
            val newMainRows = focusedNode.children
            val firstChild = newMainRows.firstOrNull()
            _state.update { s ->
                s.copy(
                    path = newPath,
                    mainRows = newMainRows,
                    focusedIndex = 0,
                    preview = firstChild
                )
            }
        }
    }

    private fun navigateBack(exitOnRoot: Boolean) {
        val currentState = _state.value
        if (currentState.path.size > 1) {
            val poppedNode = currentState.path.last()
            val newPath = currentState.path.dropLast(1)
            val parentNode = newPath.last()
            val parentChildren = parentNode.children
            val restoredIndex = parentChildren.indexOfFirst { it.id == poppedNode.id }.coerceAtLeast(0)
            val restoredPreview = parentChildren.getOrNull(restoredIndex)

            _state.update { s ->
                s.copy(
                    path = newPath,
                    mainRows = parentChildren,
                    focusedIndex = restoredIndex,
                    preview = restoredPreview
                )
            }
        } else if (exitOnRoot) {
            viewModelScope.launch {
                _effects.emit(SettingsEffect.ExitScreen)
            }
        }
    }

    private fun load() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        runCatching { getRootSettingsUseCase() }
            .onSuccess { roots ->
                Log.d("xavi", "load: $roots")
                val root = SettingsNode(
                    id = "root",
                    title = "Settings",
                    summary = "All settings",
                    children = roots
                )
                _state.update{ s ->
                    s.copy(
                        isLoading = false,
                        mainRows = roots,
                        path = listOf(root),
                        focusedIndex = 0,
                        preview = roots.firstOrNull()
                    )
                }
            }
            .onFailure { _state.update { s -> s.copy(isLoading = false, error = it.message ?: "Unknown error") } }
    }
}