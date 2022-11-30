package net.nicbell.emveeaye

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * MVI View model
 * @param initialState Initial state for view model
 * @param reducer Reducer function (state, action) -> new state
 */
abstract class MVIViewModel<TIntent, TState, TAction>(
    initialState: TState,
    private val reducer: Reducer<TState, TAction>
) : ViewModel() {

    private val _state: MutableStateFlow<TState> = MutableStateFlow(initialState)

    val state: StateFlow<TState> = _state.asStateFlow()

    abstract fun onIntent(intent: TIntent)

    /**
     * Updates state using the supplied action
     */
    protected suspend fun updateState(action: TAction) = _state.emit(reducer(_state.value, action))

    /**
     * Helper for suspend functions in view model scope using the current state
     */
    protected fun withState(block: suspend (TState) -> Unit) {
        viewModelScope.launch { block(_state.value) }
    }
}
