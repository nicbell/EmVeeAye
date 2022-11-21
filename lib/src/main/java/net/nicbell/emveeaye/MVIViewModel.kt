package net.nicbell.emveeaye

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class MVIViewModel<TIntent : Any, TState : Any, TEvent : Any>(
    initialState: TState
) : ViewModel() {

    private val _events: MutableSharedFlow<TEvent> = MutableSharedFlow()
    private val _state: MutableStateFlow<TState> = MutableStateFlow(initialState)

    val events: SharedFlow<TEvent> = _events.asSharedFlow()
    val state: StateFlow<TState> = _state.asStateFlow()

    abstract fun onIntent(intent: TIntent)

    protected suspend fun setState(newState: TState) = _state.emit(newState)

    protected suspend fun sendEvent(event: TEvent) = _events.emit(event)

    /**
     * Perform action
     */
    protected fun action(block: suspend (TState) -> Unit) {
        viewModelScope.launch { block(state.value) }
    }

    /**
     * Perform action on a specific state
     */
    protected inline fun <reified TExpectedState : TState> actionOn(
        crossinline block: suspend (TExpectedState) -> Unit,
        crossinline onError: suspend () -> Unit = {}
    ) {
        viewModelScope.launch {
            state.value.let {
                when (it) {
                    is TExpectedState -> block(it)
                    else -> onError()
                }
            }
        }
    }

    /**
     * Perform action on a specific state
     */
    protected inline fun <reified TExpectedState : TState> actionOn(
        crossinline block: suspend (TExpectedState) -> Unit
    ) {
        viewModelScope.launch {
            state.value.let {
                when (it) {
                    is TExpectedState -> block(it)
                    else -> {
                        val expectedState = TExpectedState::class.logName()
                        val receivedState = it::class.logName()
                        throw IllegalStateException("Expected: [$expectedState] Received: [$receivedState]")
                    }
                }
            }
        }
    }

    /**
     * Simple state check before executing block
     */
    protected inline fun <reified TExpectedState : TState> onState(
        block: (TExpectedState) -> Unit
    ) {
        state.value.let {
            if (it is TExpectedState) block(it)
        }
    }
}