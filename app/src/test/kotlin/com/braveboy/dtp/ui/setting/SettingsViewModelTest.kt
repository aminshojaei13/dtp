package com.braveboy.dtp.ui.setting

import com.braveboy.dtp.domain.model.SettingsNode
import com.braveboy.dtp.domain.repository.SettingsRepository
import com.braveboy.dtp.domain.usecase.GetRootSettingsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SettingsViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    private val homeNet = SettingsNode(
        id = "homenet", title = "HomeNet",
        children = listOf(
            SettingsNode(id = "signal", title = "Signal strength", value = "Good"),
            SettingsNode(id = "security", title = "Security", value = "WPA2"),
        )
    )
    private val wifi = SettingsNode(
        id = "wifi", title = "Wi-Fi",
        children = listOf(homeNet, SettingsNode(id = "guest", title = "Guest"))
    )
    private val ethernet = SettingsNode(
        id = "ethernet", title = "Ethernet",
        children = listOf(SettingsNode(id = "eth-status", title = "Connection status"))
    )
    private val network = SettingsNode(
        id = "network", title = "Network & Internet",
        children = listOf(wifi, ethernet)
    )
    private val display = SettingsNode(id = "display", title = "Display & Sound")
    private val roots = listOf(network, display)

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class FakeRepository(
        private val roots: List<SettingsNode>,
        private val error: Throwable? = null,
    ) : SettingsRepository {
        override suspend fun loadRoot(): List<SettingsNode> {
            error?.let { throw it }
            return roots
        }
    }

    private fun createViewModel(
        roots: List<SettingsNode> = this.roots,
        error: Throwable? = null,
    ) = SettingsViewModel(GetRootSettingsUseCase(FakeRepository(roots, error)))

    @Test
    fun initialLoad_showsRootRowsWithFirstFocusedAndPreviewed() = runTest(dispatcher) {
        val vm = createViewModel()

        val state = vm.state.value
        assertEquals(false, state.isLoading)
        assertEquals(roots, state.mainRows)
        assertEquals(0, state.focusedIndex)
        assertEquals(network, state.preview)
        assertEquals(1, state.path.size)
    }

    @Test
    fun loadFailure_setsErrorState() = runTest(dispatcher) {
        val vm = createViewModel(error = IllegalStateException("boom"))

        assertEquals("boom", vm.state.value.error)
        assertEquals(false, vm.state.value.isLoading)
    }

    @Test
    fun down_movesFocusAndPreview_andDoesNotWrapAtEnd() = runTest(dispatcher) {
        val vm = createViewModel()

        vm.onIntent(SettingsIntent.Down)
        assertEquals(1, vm.state.value.focusedIndex)
        assertEquals(display, vm.state.value.preview)

        vm.onIntent(SettingsIntent.Down)
        assertEquals(1, vm.state.value.focusedIndex)
    }

    @Test
    fun up_doesNotWrapAtStart() = runTest(dispatcher) {
        val vm = createViewModel()

        vm.onIntent(SettingsIntent.Up)
        assertEquals(0, vm.state.value.focusedIndex)
        assertEquals(network, vm.state.value.preview)
    }

    @Test
    fun forward_onRowWithChildren_entersNextLevel() = runTest(dispatcher) {
        val vm = createViewModel()

        vm.onIntent(SettingsIntent.Forward)

        val state = vm.state.value
        assertEquals(listOf("root", "network"), state.path.map { it.id })
        assertEquals(listOf(wifi, ethernet), state.mainRows)
        assertEquals(0, state.focusedIndex)
        assertEquals(wifi, state.preview)
    }

    @Test
    fun forward_onLeafRow_doesNothing() = runTest(dispatcher) {
        val vm = createViewModel()
        vm.onIntent(SettingsIntent.Forward) // network
        vm.onIntent(SettingsIntent.Down)     // ethernet
        vm.onIntent(SettingsIntent.Forward)  // ethernet -> connection status list
        vm.onIntent(SettingsIntent.Forward)  // connection status is a leaf

        assertEquals(3, vm.state.value.path.size)
        assertEquals(listOf(SettingsNode(id = "eth-status", title = "Connection status")), vm.state.value.mainRows)
    }

    @Test
    fun right_atRootLevel_isConsumedWithoutExiting() = runTest(dispatcher) {
        val vm = createViewModel()
        val effects = mutableListOf<SettingsEffect>()
        backgroundScope.launch(dispatcher) { vm.effects.collect { effects += it } }

        vm.onIntent(SettingsIntent.Right)

        assertTrue(effects.isEmpty())
        assertEquals(1, vm.state.value.path.size)
        assertEquals(roots, vm.state.value.mainRows)
    }

    @Test
    fun back_atRootLevel_emitsExitScreen() = runTest(dispatcher) {
        val vm = createViewModel()
        val effects = mutableListOf<SettingsEffect>()
        backgroundScope.launch(dispatcher) { vm.effects.collect { effects += it } }

        vm.onIntent(SettingsIntent.Back)

        assertEquals(listOf<SettingsEffect>(SettingsEffect.ExitScreen), effects)
    }

    @Test
    fun right_atDeeperLevel_goesBackLikeBack() = runTest(dispatcher) {
        val vm = createViewModel()
        vm.onIntent(SettingsIntent.Forward) // network

        vm.onIntent(SettingsIntent.Right)

        assertEquals(1, vm.state.value.path.size)
        assertEquals(roots, vm.state.value.mainRows)
    }

    @Test
    fun back_restoresFocusOnTheRowWeLeft() = runTest(dispatcher) {
        // Spec path: Network & Internet -> Wi-Fi -> HomeNet -> Signal strength -> Back
        val vm = createViewModel()
        vm.onIntent(SettingsIntent.Forward)   // network -> [wifi, ethernet]
        vm.onIntent(SettingsIntent.Forward)   // wifi -> [homenet, guest]
        vm.onIntent(SettingsIntent.Forward)   // homenet -> [signal, security]
        vm.onIntent(SettingsIntent.Down)      // security focused
        vm.onIntent(SettingsIntent.Back)      // back to [homenet, guest]

        val state = vm.state.value
        assertEquals(listOf("root", "network", "wifi"), state.path.map { it.id })
        assertEquals(listOf(homeNet, SettingsNode(id = "guest", title = "Guest")), state.mainRows)
        assertEquals(0, state.focusedIndex)
        assertEquals(homeNet, state.preview)
    }

    @Test
    fun focusRow_updatesPreviewAndClampsIndex() = runTest(dispatcher) {
        val vm = createViewModel()

        vm.onIntent(SettingsIntent.FocusRow(99))

        assertEquals(1, vm.state.value.focusedIndex)
        assertEquals(display, vm.state.value.preview)
    }

    @Test
    fun emptyRoot_keepsStateStable() = runTest(dispatcher) {
        val vm = createViewModel(roots = emptyList())

        vm.onIntent(SettingsIntent.Down)
        vm.onIntent(SettingsIntent.Forward)

        assertEquals(0, vm.state.value.focusedIndex)
        assertNull(vm.state.value.preview)
    }
}
