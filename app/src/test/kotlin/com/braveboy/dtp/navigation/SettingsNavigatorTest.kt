package com.braveboy.dtp.navigation

import com.braveboy.dtp.domain.model.SettingsNode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsNavigatorTest {

    private val navigator = SettingsNavigator()

    private val leafChild1 = SettingsNode(id = "wifi_home", title = "Home Wi-Fi", value = "Connected")
    private val leafChild2 = SettingsNode(id = "wifi_guest", title = "Guest Wi-Fi")
    private val wifiNode = SettingsNode(
        id = "wifi",
        title = "Wi-Fi",
        children = listOf(leafChild1, leafChild2)
    )
    private val bluetoothNode = SettingsNode(id = "bluetooth", title = "Bluetooth")
    private val rootNode = SettingsNode(
        id = "root",
        title = "Settings",
        children = listOf(wifiNode, bluetoothNode)
    )

    private val initialState = SettingsNavigationState(
        path = listOf(rootNode),
        focusedRowId = wifiNode.id,
        focusedIndex = 0
    )

    @Test
    fun moveDown_changesFocusedIndex() {
        val result = navigator.move(initialState, 1)
        assertTrue(result is NavigationResult.Changed)
        val newState = (result as NavigationResult.Changed).state
        assertEquals(1, newState.focusedIndex)
        assertEquals("bluetooth", newState.focusedRowId)
    }

    @Test
    fun moveUpAtTop_returnsNoOp() {
        val result = navigator.move(initialState, -1)
        assertEquals(NavigationResult.NoOp, result)
    }

    @Test
    fun moveDownAtBottom_returnsNoOp() {
        val bottomState = initialState.copy(focusedIndex = 1, focusedRowId = bluetoothNode.id)
        val result = navigator.move(bottomState, 1)
        assertEquals(NavigationResult.NoOp, result)
    }

    @Test
    fun forwardOnNodeWithChildren_pushesPathAndResetsFocus() {
        val result = navigator.forward(initialState)
        assertTrue(result is NavigationResult.Changed)
        val newState = (result as NavigationResult.Changed).state
        assertEquals(2, newState.path.size)
        assertEquals("wifi", newState.path.last().id)
        assertEquals("wifi_home", newState.focusedRowId)
        assertEquals(0, newState.focusedIndex)
    }

    @Test
    fun forwardOnLeafNode_returnsNoOp() {
        val leafState = SettingsNavigationState(
            path = listOf(rootNode, wifiNode),
            focusedRowId = leafChild1.id,
            focusedIndex = 0
        )
        val result = navigator.forward(leafState)
        assertEquals(NavigationResult.NoOp, result)
    }

    @Test
    fun backFromLevel2_popsPathAndRestoresPreviousFocus() {
        val level2State = SettingsNavigationState(
            path = listOf(rootNode, wifiNode),
            focusedRowId = leafChild1.id,
            focusedIndex = 0
        )
        val result = navigator.back(level2State)
        assertTrue(result is NavigationResult.Changed)
        val newState = (result as NavigationResult.Changed).state
        assertEquals(1, newState.path.size)
        assertEquals("wifi", newState.focusedRowId)
        assertEquals(0, newState.focusedIndex)
    }

    @Test
    fun backAtLevel1_returnsExit() {
        val result = navigator.back(initialState)
        assertEquals(NavigationResult.Exit, result)
    }

    @Test
    fun rightFromLevel2_popsPathAndRestoresFocus() {
        val level2State = SettingsNavigationState(
            path = listOf(rootNode, wifiNode),
            focusedRowId = leafChild1.id,
            focusedIndex = 0
        )
        val result = navigator.right(level2State)
        assertTrue(result is NavigationResult.Changed)
        val newState = (result as NavigationResult.Changed).state
        assertEquals(1, newState.path.size)
        assertEquals("wifi", newState.focusedRowId)
    }

    @Test
    fun rightAtLevel1_returnsNoOp() {
        val result = navigator.right(initialState)
        assertEquals(NavigationResult.NoOp, result)
    }

    @Test
    fun focus_validIndex_updatesFocus() {
        val result = navigator.focus(initialState, 1)
        assertTrue(result is NavigationResult.Changed)
        val newState = (result as NavigationResult.Changed).state
        assertEquals(1, newState.focusedIndex)
        assertEquals("bluetooth", newState.focusedRowId)
    }
}
