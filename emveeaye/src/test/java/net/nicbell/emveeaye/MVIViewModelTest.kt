package net.nicbell.emveeaye

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.test.runTest
import kotlinx.parcelize.Parcelize
import net.nicbell.emveeaye.test.ViewModelTest
import org.junit.Assert
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
            TestState(testData)
        )
    }

    @Test
    fun testStateError() = runTest {
        // WHEN
        vm.onIntent(TestIntent.DoSomethingElse)

        // THEN
        merge(vm.state).assertFlow(
            TestState(),
            TestState(errorMessage = testError)
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
            TestState(data = testData),
            TestState(data = testData, message = testMessage)
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
            TestState(errorMessage = testError),
            TestState(errorMessage = null)
        )
    }

    @Test
    fun testSaveState() = runTest {
        // GIVEN
        val stateHandle = SavedStateHandle()
        val vm = TestViewModel(stateHandle)

        // WHEN
        vm.onIntent(TestIntent.LoadContent)

        // THEN
        vm.state.assertFlow(
            TestState(),
            TestState(data = testData)
        )

        val savedState = stateHandle.get<TestState>(SavedStateHandle.UI_STATE_KEY)
        Assert.assertEquals(TestState(data = testData), savedState)
    }

    @Test
    fun testRestoreAndSaveState() = runTest {
        // GIVEN
        val restoredState = TestState(listOf("Previous state"))
        val stateHandle = SavedStateHandle(mapOf(SavedStateHandle.UI_STATE_KEY to restoredState))
        val vm = TestViewModel(stateHandle)

        // WHEN
        vm.onIntent(TestIntent.LoadContent)

        // THEN
        // Check flow
        vm.state.assertFlow(
            restoredState,
            restoredState.copy(data = testData)
        )

        // Check saved sate
        val savedState = stateHandle.get<TestState>(SavedStateHandle.UI_STATE_KEY)
        Assert.assertEquals(restoredState.copy(data = testData), savedState)
    }

    /**
     * Reducer function
     */
    class TestStateReducer : Reducer<TestState, TestAction> {
        override fun invoke(state: TestState, action: TestAction) = when (action) {
            is TestAction.ShowData -> state.copy(data = action.data)
            is TestAction.ShowError -> state.copy(errorMessage = action.message)
            is TestAction.ShowMessage -> state.copy(message = action.message)
            TestAction.HideErrorMessage -> state.copy(errorMessage = null)
            TestAction.HideMessage -> state.copy(message = null)
        }
    }

    /**
     * Test view model
     */
    class TestViewModel(savedStateHandle: SavedStateHandle? = null) :
        MVIViewModel<TestIntent, TestState, TestAction>(
            initialState = TestState(),
            reducer = TestStateReducer(),
            savedStateHandle = savedStateHandle
        ) {
        override fun onIntent(intent: TestIntent) = when (intent) {
            TestIntent.LoadContent -> handleContentAction()
            TestIntent.DoSomethingElse -> handleDoSomethingElse()
            TestIntent.MessageShown -> handleMessageShown()
            TestIntent.ErrorMessageShown -> handleErrorMessageShown()
        }

        private fun handleContentAction() = withState {
            updateState(TestAction.ShowData(testData))
        }

        private fun handleDoSomethingElse() = withState { state ->
            if (state.data.any()) {
                updateState(TestAction.ShowMessage(testMessage))
            } else {
                updateState(TestAction.ShowError(testError))
            }
        }

        private fun handleMessageShown() = withState {
            updateState(TestAction.HideMessage)
        }

        private fun handleErrorMessageShown() = withState {
            updateState(TestAction.HideErrorMessage)
        }
    }

    /**
     * Test state using @Parcelize so that is can be saved
     */
    @Parcelize
    internal data class TestState(
        val data: List<String> = emptyList(),
        val message: UIString? = null,
        val errorMessage: UIString? = null
    ) : Parcelable

    /**
     * Test intents
     */
    internal sealed class TestIntent {
        data object LoadContent : TestIntent()
        data object DoSomethingElse : TestIntent()
        data object MessageShown : TestIntent()
        data object ErrorMessageShown : TestIntent()
    }

    /**
     * Test actions
     */
    sealed class TestAction {
        data class ShowData(val data: List<String>) : TestAction()
        data class ShowMessage(val message: UIString) : TestAction()
        data class ShowError(val message: UIString) : TestAction()
        data object HideMessage : TestAction()
        data object HideErrorMessage : TestAction()
    }

    companion object {
        private val testData = listOf("Test", "Test 2")
        private val testMessage = UIString.ActualString("I'm fully loaded.")
        private val testError = UIString.ActualString("I feel empty.")
    }
}