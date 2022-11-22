package net.nicbell.emveeaye

import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.test.runTest
import net.nicbell.emveeaye.test.ViewModelTest
import org.junit.Test

internal class MVIViewModelTest : ViewModelTest() {

    internal sealed class TestState {
        object Empty : TestState()
        data class Loaded(val data: List<Any>) : TestState()
    }

    internal sealed class TestIntent {
        object LoadContent : TestIntent()
        object DoSomething : TestIntent()
        object DoSomethingElse : TestIntent()
    }

    internal sealed class TestEvent {
        data class Error(val message: String) : TestEvent()
    }

    private val vm = object : MVIViewModel<TestIntent, TestState, Any>(TestState.Empty) {
        override fun onIntent(intent: TestIntent) = when (intent) {
            TestIntent.LoadContent -> loadContentAction()
            TestIntent.DoSomething -> doSomethingAction()
            TestIntent.DoSomethingElse -> doSomethingElseAction()
        }

        private fun loadContentAction() = actionOn<TestState.Empty> {
            setState(TestState.Loaded(emptyList()))
        }

        private fun doSomethingAction() = action {
            onState<TestState.Empty> {
                sendEvent(TestEvent.Error("I don't want to do anything."))
            }

            onState<TestState.Loaded> {
                sendEvent(TestEvent.Error("I've done so much."))
            }
        }

        private fun doSomethingElseAction() = actionOn<TestState.Loaded>(
            block = {
                sendEvent(TestEvent.Error("I'm fully loaded."))
            },
            onError = {
                sendEvent(TestEvent.Error("I was not even loaded."))
            }
        )
    }

    @Test
    fun testStatePublishing() = runTest {
        // WHEN
        vm.onIntent(TestIntent.LoadContent)

        // THEN
        merge(vm.state, vm.events).assertFlow(
            TestState.Empty,
            TestState.Loaded(emptyList())
        )
    }

    @Test
    fun testEventPublishing() = runTest {
        // WHEN
        vm.onIntent(TestIntent.DoSomething)

        // THEN
        merge(vm.state, vm.events).assertFlow(
            TestState.Empty,
            TestEvent.Error("I don't want to do anything.")
        )
    }

    @Test
    fun testStateErrorHandlingError() = runTest {
        // WHEN
        vm.onIntent(TestIntent.DoSomethingElse)

        // THEN
        merge(vm.state, vm.events).assertFlow(
            TestState.Empty,
            TestEvent.Error("I was not even loaded.")
        )
    }

    @Test
    fun testStateErrorHandlingSuccess() = runTest {
        // WHEN
        vm.onIntent(TestIntent.LoadContent)
        vm.onIntent(TestIntent.DoSomethingElse)

        // THEN
        merge(vm.state, vm.events).assertFlow(
            TestState.Empty,
            TestState.Loaded(emptyList()),
            TestEvent.Error("I'm fully loaded.")
        )
    }
}