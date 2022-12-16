package net.nicbell.emveeaye.test

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Rule
import org.junit.rules.TestWatcher
import org.junit.runner.Description

open class ViewModelTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutinesRule = DefaultTestDispatchersRule()

    suspend fun Flow<*>.assertFlow(vararg flowItems: Any) {
        this.test {
            flowItems.forEach {
                Assert.assertEquals(it, awaitItem())
            }
        }
    }

    @ExperimentalCoroutinesApi
    class DefaultTestDispatchersRule(
        private val testDispatcher: TestDispatcher = StandardTestDispatcher()
    ) : TestWatcher() {
        override fun starting(description: Description) {
            Dispatchers.setMain(testDispatcher)
        }

        override fun finished(description: Description) {
            Dispatchers.resetMain()
        }
    }
}