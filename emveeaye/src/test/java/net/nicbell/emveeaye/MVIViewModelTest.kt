package net.nicbell.emveeaye

import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.test.runTest
import net.nicbell.emveeaye.test.ViewModelTest
import org.junit.Test

internal class MVIViewModelTest : ViewModelTest() {

    private val vm = TestViewModel()

    @Test
    fun testStateUpdate() = runTest {
        // WHEN
        vm.onIntent(TestIntent.LoadContent)

        // THEN
        vm.state.assertFlow(
            TestState(),
            TestState(listOf("Test"))
        )
    }


    @Test
    fun testStateError() = runTest {
        // WHEN
        vm.onIntent(TestIntent.DoSomethingElse)

        // THEN
        merge(vm.state).assertFlow(
            TestState(),
            TestState(errorMessage = "I feel empty.")
        )
    }

    @Test
    fun testStateMessage() = runTest {
        // WHEN
        vm.onIntent(TestIntent.LoadContent)
        vm.onIntent(TestIntent.DoSomethingElse)

        // THEN
        merge(vm.state).assertFlow(
            TestState(),
            TestState(data = listOf("Test")),
            TestState(data = listOf("Test"), message = "I'm fully loaded.")
        )
    }

    @Test
    fun testStateErrorShown() = runTest {
        // WHEN
        vm.onIntent(TestIntent.DoSomethingElse)
        vm.onIntent(TestIntent.ErrorMessageShown)

        // THEN
        merge(vm.state).assertFlow(
            TestState(),
            TestState(errorMessage = "I feel empty."),
            TestState(errorMessage = null)
        )
    }

    class TestStateReducer : Reducer<TestState, TestAction> {
        override fun invoke(state: TestState, action: TestAction) = when (action) {
            is TestAction.ShowData -> state.copy(data = action.data)
            is TestAction.ShowError -> state.copy(errorMessage = action.message)
            is TestAction.ShowMessage -> state.copy(message = action.message)
            TestAction.HideErrorMessage -> state.copy(errorMessage = null)
        }
    }

    class TestViewModel : MVIViewModel<TestIntent, TestState, TestAction>(
        initialState = TestState(),
        reducer = TestStateReducer()
    ) {
        override fun onIntent(intent: TestIntent) = when (intent) {
            TestIntent.LoadContent -> handleContentAction()
            TestIntent.DoSomethingElse -> handleDoSomethingElse()
            TestIntent.ErrorMessageShown -> handleErrorMessageShown()
            TestIntent.MessageShown -> TODO()
        }

        private fun handleContentAction() = withState {
            updateState(TestAction.ShowData(listOf("Test")))
        }

        private fun handleDoSomethingElse() = withState {
            if (it.data.any()) {
                updateState(TestAction.ShowMessage("I'm fully loaded."))
            } else {
                updateState(TestAction.ShowError("I feel empty."))
            }
        }

        private fun handleErrorMessageShown() = withState {
            updateState(TestAction.HideErrorMessage)
        }
    }

    internal data class TestState(
        val data: List<String> = emptyList(),
        val message: String? = null,
        val errorMessage: String? = null
    )

    internal sealed class TestIntent {
        object LoadContent : TestIntent()
        object DoSomethingElse : TestIntent()
        object MessageShown : TestIntent()
        object ErrorMessageShown : TestIntent()
    }

    sealed class TestAction {
        data class ShowData(val data: List<String>) : TestAction()
        data class ShowError(val message: String) : TestAction()
        data class ShowMessage(val message: String) : TestAction()
        object HideErrorMessage : TestAction()
    }
}