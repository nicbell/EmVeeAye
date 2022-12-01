package net.nicbell.emveeaye

/**
 * Pure function (state, action) -> new state
 */
typealias Reducer<TState, TAction> = (state: TState, action: TAction) -> TState