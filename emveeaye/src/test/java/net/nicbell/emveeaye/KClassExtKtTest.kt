package net.nicbell.emveeaye

import org.junit.Assert
import org.junit.Test

internal class KClassExtKtTest {

    sealed class TestParent {
        data object TestChild : TestParent()
    }

    @Test
    fun logName() {
        Assert.assertEquals("TestParent.TestChild", TestParent.TestChild::class.logName())
    }
}