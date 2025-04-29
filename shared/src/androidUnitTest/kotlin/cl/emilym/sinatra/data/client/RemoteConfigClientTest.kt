package cl.emilym.sinatra.data.client

import cl.emilym.sinatra.RemoteConfigNotLoadedException
import io.mockk.*
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class RemoteConfigClientTest {

    private lateinit var wrapper: RemoteConfigWrapper
    private lateinit var client: RemoteConfigClient

    @BeforeTest
    fun setup() {
        wrapper = mockk()
        client = RemoteConfigClient(wrapper)
    }

    @Test
    fun `load returns true on success`() = runTest {
        coEvery { wrapper.load() } just Runs

        val result = client.load()

        assertTrue(result)
        coVerify { wrapper.load() }
    }

    @Test
    fun `load returns false on exception`() = runTest {
        coEvery { wrapper.load() } throws Exception("Failed to load")

        val result = client.load()

        assertFalse(result)
        coVerify { wrapper.load() }
    }

    @Test
    fun `string returns value when loaded and exists`() = runTest {
        coEvery { wrapper.load() } just Runs
        every { wrapper.exists("key") } returns true
        every { wrapper.string("key") } returns "value"

        val result = client.string("key")

        assertEquals("value", result)
    }

    @Test
    fun `string returns null if load fails`() = runTest {
        coEvery { wrapper.load() } throws Exception("Failed to load")

        val result = client.string("key")

        assertNull(result)
    }

    @Test
    fun `string returns null if key does not exist`() = runTest {
        coEvery { wrapper.load() } just Runs
        every { wrapper.exists("key") } returns false

        val result = client.string("key")

        assertNull(result)
    }

    @Test
    fun `string returns null if getter throws`() = runTest {
        coEvery { wrapper.load() } just Runs
        every { wrapper.exists("key") } returns true
        every { wrapper.string("key") } throws Exception("Getter failed")

        val result = client.string("key")

        assertNull(result)
    }

    @Test
    fun `stringImmediate returns value when loaded and exists`() {
        every { wrapper.loaded } returns true
        every { wrapper.exists("key") } returns true
        every { wrapper.string("key") } returns "value"

        val result = client.stringImmediate("key")

        assertEquals("value", result)
    }

    @Test
    fun `stringImmediate throws when not loaded`() {
        every { wrapper.loaded } returns false

        assertFailsWith<RemoteConfigNotLoadedException> {
            client.stringImmediate("key")
        }
    }

    @Test
    fun `stringImmediate returns null if key does not exist`() {
        every { wrapper.loaded } returns true
        every { wrapper.exists("key") } returns false

        val result = client.stringImmediate("key")

        assertNull(result)
    }

    @Test
    fun `stringImmediate returns null if getter throws`() {
        every { wrapper.loaded } returns true
        every { wrapper.exists("key") } returns true
        every { wrapper.string("key") } throws Exception("Getter failed")

        val result = client.stringImmediate("key")

        assertNull(result)
    }

    @Test
    fun `boolean returns value when loaded and exists`() = runTest {
        coEvery { wrapper.load() } just Runs
        every { wrapper.exists("boolKey") } returns true
        every { wrapper.boolean("boolKey") } returns true

        val result = client.boolean("boolKey")

        assertTrue(result!!)
    }

    @Test
    fun `numberImmediate returns value when loaded and exists`() {
        every { wrapper.loaded } returns true
        every { wrapper.exists("numKey") } returns true
        every { wrapper.number("numKey") } returns 42.0

        val result = client.numberImmediate("numKey")

        assertEquals(42.0, result)
    }
}
