package net.nicbell.emveeaye

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.testing.TestLifecycleOwner
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import net.nicbell.emveeaye.test.ViewModelTest
import org.junit.Assert
import org.junit.Test

internal class LifecycleExtKtTest : ViewModelTest() {
    @Test
    fun lifeCycleFlow() = runTest {
        val flowData: MutableStateFlow<String> = MutableStateFlow("Test")
        val fragment = mockk<Fragment>(relaxed = true)
        val owner = TestLifecycleOwner()
        every { fragment.viewLifecycleOwner } returns owner

        LifecycleRegistry.createUnsafe(owner).handleLifecycleEvent(Lifecycle.Event.ON_START)

        fragment.observeFlow(flowData) {
            println(it)
            Assert.assertEquals("Test", it)
            LifecycleRegistry.createUnsafe(owner).handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        }
    }
}