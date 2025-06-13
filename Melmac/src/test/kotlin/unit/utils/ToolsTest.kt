import io.mockk.*
import org.junit.jupiter.api.*
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Paths
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import utils.Tools

class ToolsTest {

    @Test
    fun `run returns output of command`() {
        val output = Tools.run("echo hello")
        assertTrue(output.trim() == "hello")
    }

    @Test
    fun `run returns error message on failure`() {
        val output = Tools.run("nonexistentcommand")
        assertTrue(output.startsWith("ERROR:"))
    }

    @Test
    fun `isMac returns true on macOS`() {
        val original = System.getProperty("os.name")
        try {
            System.setProperty("os.name", "Mac OS X")
            assertTrue(Tools.isMac())
        } finally {
            System.setProperty("os.name", original)
        }
    }

    @Test
    fun `isMac returns false on non-macOS`() {
        val original = System.getProperty("os.name")
        try {
            System.setProperty("os.name", "Linux")
            assertFalse(Tools.isMac())
        } finally {
            System.setProperty("os.name", original)
        }
    }

    @Test
    fun `getSimulatorIdforIOS returns correct id from simctl output`() {
        mockkConstructor(ProcessBuilder::class)
        val simName = "iPhone 14"
        val expectedId = "1234-5678-ABCD"
        val simctlOutput = """
            == Devices ==
            -- iOS 16.0 --
            $simName ($expectedId) (Booted)
        """.trimIndent()
        every { anyConstructed<ProcessBuilder>().start() } returns mockk {
            every { waitFor() } returns 0
            every { inputStream } returns ByteArrayInputStream(simctlOutput.toByteArray())
        }
        val id = Tools.getSimulatorIdforIOS(simName)
        assertEquals(expectedId, id)
        unmockkConstructor(ProcessBuilder::class)
    }

    @Test
    fun `getSimulatorIdforIOS throws if not found`() {
        mockkConstructor(ProcessBuilder::class)
        every { anyConstructed<ProcessBuilder>().start() } returns mockk {
            every { waitFor() } returns 0
            every { inputStream } returns ByteArrayInputStream("== Devices ==".toByteArray())
        }
        assertThrows(Exception::class.java) {
            Tools.getSimulatorIdforIOS("Nonexistent")
        }
        unmockkConstructor(ProcessBuilder::class)
    }

    @Test
    fun `getEmulatorSerial returns serial for matching emulator`() {
        mockkConstructor(ProcessBuilder::class)
        val serial = "emulator-5554"
        val avdName = "Pixel_5"
        val adbDevicesOutput = "$serial\tdevice\n"
        val getpropOutput = avdName

        every { anyConstructed<ProcessBuilder>().start() } returnsMany listOf(
            mockk {
                every { inputStream } returns ByteArrayInputStream(adbDevicesOutput.toByteArray())
            },
            mockk {
                every { inputStream } returns ByteArrayInputStream(getpropOutput.toByteArray())
            }
        )

        val result = Tools.getEmulatorSerial(avdName)
        assertEquals(serial, result)
        unmockkConstructor(ProcessBuilder::class)
    }

    @Test
    fun `getEmulatorSerial returns null if no match`() {
        mockkConstructor(ProcessBuilder::class)
        val adbDevicesOutput = "emulator-5554\tdevice\n"
        val getpropOutput = "Other_AVD"
        every { anyConstructed<ProcessBuilder>().start() } returnsMany listOf(
            mockk {
                every { inputStream } returns ByteArrayInputStream(adbDevicesOutput.toByteArray())
            },
            mockk {
                every { inputStream } returns ByteArrayInputStream(getpropOutput.toByteArray())
            }
        )
        val result = Tools.getEmulatorSerial("NoMatch")
        assertNull(result)
        unmockkConstructor(ProcessBuilder::class)
    }

    @Test
    fun `getApkVersion returns version from aapt output`() {
        mockkObject(Tools)
        every { Tools.resolvePath(any()) } returns "/mock/aapt"
        mockkConstructor(ProcessBuilder::class)
        val aaptOutput = "package: name='com.example' versionName='1.2.3'"
        every { anyConstructed<ProcessBuilder>().start() } returns mockk {
            every { inputStream } returns ByteArrayInputStream(aaptOutput.toByteArray())
        }
        val version = Tools.getApkVersion("/fake/path.apk")
        assertEquals("1.2.3", version)
        unmockkConstructor(ProcessBuilder::class)
        unmockkObject(Tools)
    }

    @Test
    fun `getApkVersion returns null if not found`() {
        mockkObject(Tools)
        every { Tools.resolvePath(any()) } returns "/mock/aapt"
        mockkConstructor(ProcessBuilder::class)
        every { anyConstructed<ProcessBuilder>().start() } returns mockk {
            every { inputStream } returns ByteArrayInputStream("no version".toByteArray())
        }
        val version = Tools.getApkVersion("/fake/path.apk")
        assertNull(version)
        unmockkConstructor(ProcessBuilder::class)
        unmockkObject(Tools)
    }

    @Test
    fun `getAppBundleVersion returns version from PlistBuddy output`() {
        mockkConstructor(ProcessBuilder::class)
        every { anyConstructed<ProcessBuilder>().start() } returns mockk {
            every { inputStream } returns ByteArrayInputStream("2.5.1".toByteArray())
        }
        val version = Tools.getAppBundleVersion("/fake/path.app")
        assertEquals("2.5.1", version)
        unmockkConstructor(ProcessBuilder::class)
    }

    @Test
    fun `getAppBundleVersion returns null if output is empty`() {
        mockkConstructor(ProcessBuilder::class)
        every { anyConstructed<ProcessBuilder>().start() } returns mockk {
            every { inputStream } returns ByteArrayInputStream("".toByteArray())
        }
        val version = Tools.getAppBundleVersion("/fake/path.app")
        assertNull(version)
        unmockkConstructor(ProcessBuilder::class)
    }
}