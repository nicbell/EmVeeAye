package net.nicbell.emveeaye

import android.content.res.Resources
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Test

internal class UIStringTest {
    @Test
    fun testActualString() {
        // GIVEN
        val resources = mockk<Resources>(relaxed = true)
        val uiString = UIString.ActualString("Test")

        // WHEN
        val result = uiString.getString(resources)

        // THEN
        Assert.assertEquals("Test", result)
    }

    @Test
    fun testResourceString() {
        // GIVEN
        val resources = mockk<Resources>(relaxed = true)
        val uiString = UIString.StringResource(1)
        every { resources.getString(1) } returns "Test"

        // WHEN
        val result = uiString.getString(resources)

        // THEN
        Assert.assertEquals("Test", result)
    }

    @Test
    fun testResourceStringExt() {
        // GIVEN
        val resources = mockk<Resources>(relaxed = true)
        val uiString = UIString.StringResource(1)
        every { resources.getString(1) } returns "Test"

        // WHEN
        val result = resources.getString(uiString)

        // THEN
        Assert.assertEquals("Test", result)
    }
}