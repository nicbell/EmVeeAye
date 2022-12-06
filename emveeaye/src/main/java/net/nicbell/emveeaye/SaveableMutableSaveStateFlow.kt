package net.nicbell.emveeaye

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class SaveableMutableSaveStateFlow<T>(
    private val savedStateHandle: SavedStateHandle? = null,
    private val key: String = "ui_state",
    defaultValue: T
) {
    private val _state: MutableStateFlow<T> =
        MutableStateFlow(savedStateHandle?.get<T>(key) ?: defaultValue)

    suspend fun emit(value: T) {
        _state.emit(value)
        savedStateHandle?.set(key, value)
    }

    val value: T
        get() = _state.value

    fun asStateFlow(): StateFlow<T> = _state.asStateFlow()
}