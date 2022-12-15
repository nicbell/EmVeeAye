package net.nicbell.emveeaye

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * MVI View model
 * @param initialState Initial state for view model if no state is restored
 * @param reducer Reducer function (state, action) -> new state
 * @param savedStateHandle Allowed for restoring and saving state `null` by default
 */
abstract class MVIViewModel<TIntent, TState, TAction>(
    initialState: TState,
    private val reducer: Reducer<TState, TAction>,
    private val savedStateHandle: SavedStateHandle? = null
) : ViewModel() {

    private val _state = MutableStateFlow(savedStateHandle?.getUiState<TState>() ?: initialState)

    /**
     * Observable read-only state
     */
    val state: StateFlow<TState> = _state.asStateFlow()

    /**
     * Submit intent to be handled by the view model
     * @param intent `TIntent` to be handled
     */
    abstract fun onIntent(intent: TIntent)

    /**
     * Updates state using the supplied action
     * @param action `TAction` used by reducer to update the state
     */
    protected suspend fun updateState(action: TAction) {
        val newState = reducer(_state.value, action)
        _state.emit(newState)
        savedStateHandle?.setUiState(newState)
    }

    /**
     * Helper for suspend functions in view model scope using the current state
     */
    protected fun withState(block: suspend (TState) -> Unit) {
        viewModelScope.launch { block(_state.value) }
    }
}