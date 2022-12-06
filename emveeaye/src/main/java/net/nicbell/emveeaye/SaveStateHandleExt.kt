package net.nicbell.emveeaye

import androidx.lifecycle.SavedStateHandle

val SavedStateHandle.Companion.UI_STATE_KEY: String
    get() = "emveeaye_ui_state"

fun <TState> SavedStateHandle.getUiState(): TState? =
    get<TState>(SavedStateHandle.UI_STATE_KEY)

fun <TState> SavedStateHandle.setUiState(value: TState) =
    set<TState>(SavedStateHandle.UI_STATE_KEY, value)