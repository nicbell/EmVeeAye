package net.nicbell.emveeaye

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

fun <T> Fragment.observeFlow(flow: Flow<T>, collector: FlowCollector<T>) {
    viewLifecycleOwner.observeFlow(flow, collector)
}

fun <T> LifecycleOwner.observeFlow(flow: Flow<T>, collector: FlowCollector<T>) {
    lifecycleScope.launch {
        flow.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect(collector)
    }
}