package com.braveboy.dtp.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.braveboy.dtp.domain.model.SettingsNode
import com.braveboy.dtp.domain.usecase.GetRootSettingsUseCase
import com.braveboy.dtp.navigation.NavigationResult
import com.braveboy.dtp.navigation.SettingsNavigationState
import com.braveboy.dtp.navigation.SettingsNavigator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val getRootSettingsUseCase: GetRootSettingsUseCase,
    private val navigator: SettingsNavigator = SettingsNavigator()
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<SettingsEffect>()
    val effects = _effects.asSharedFlow()

    private var navigation: SettingsNavigationState? = null

    init { load() }

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            SettingsIntent.Up -> transition { navigator.move(it, -1) }
            SettingsIntent.Down -> transition { navigator.move(it, 1) }
            SettingsIntent.Forward -> transition { navigator.forward(it) }
            SettingsIntent.Right -> transition { navigator.right(it) }
            SettingsIntent.Back -> transition { navigator.back(it) }
            is SettingsIntent.FocusRow -> transition { navigator.focus(it, intent.index) }
            SettingsIntent.Retry -> load()
        }
    }

    private fun transition(block: (SettingsNavigationState) -> NavigationResult) {
        val current = navigation ?: return
        when (val result = block(current)) {
            is NavigationResult.Changed -> {
                navigation = result.state
                publish(result.state)
            }
            NavigationResult.Exit -> viewModelScope.launch { _effects.emit(SettingsEffect.ExitScreen) }
            NavigationResult.NoOp -> Unit
        }
    }

    private fun load() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        runCatching { getRootSettingsUseCase() }
            .onSuccess { roots ->
                val root = SettingsNode(
                    id = "root",
                    title = "Settings",
                    summary = "All settings",
                    children = roots
                )
                val first = roots.firstOrNull()
                navigation = SettingsNavigationState(
                    path = listOf(root),
                    focusedRowId = first?.id,
                    focusedIndex = 0
                )
                navigation?.let(::publish)
            }
            .onFailure { _state.update { s -> s.copy(isLoading = false, error = it.message ?: "Unknown error") } }
    }

    private fun publish(nav: SettingsNavigationState) {
        val rows = nav.currentRows
        _state.value = SettingsState(
            isLoading = false,
            path = nav.path,
            mainRows = rows,
            focusedIndex = nav.focusedIndex,
            preview = nav.selectedRow
        )
    }
}