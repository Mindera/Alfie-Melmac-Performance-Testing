import core.runners.TestRunner
import android.AndroidTestRunner
import ios.IOSTestRunner
import dtos.TestExecutionConfigDTO
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TestRunnerTest {
    private val iosRunner = mockk<IOSTestRunner>()
    private val androidRunner = mockk<AndroidTestRunner>()
    private val testRunner = TestRunner(iosRunner, androidRunner)

    @Test
    fun `run delegates to IOSTestRunner for ios platform`() {
        val config = TestExecutionConfigDTO(
            executionTypeName = "type",
            metricName = "metric",
            metricParams = emptyMap(),
            executionTypeParams = emptyMap(),
            testThresholds = null,
            deviceName = "iPhone",
            deviceSerialNumber = "123",
            platform = "ios",
            appName = "App",
            appVersion = "1.0",
            appPackage = "pkg",
            mainActivity = null
        )
        every { iosRunner.run(config) } returns mapOf("output" to "value")
        val result = testRunner.run(config)
        assertEquals(mapOf("output" to "value"), result)
        verify(exactly = 1) { iosRunner.run(config) }
        verify(exactly = 0) { androidRunner.run(any()) }
    }

    @Test
    fun `run delegates to AndroidTestRunner for android platform`() {
        val config = TestExecutionConfigDTO(
            executionTypeName = "type",
            metricName = "metric",
            metricParams = emptyMap(),
            executionTypeParams = emptyMap(),
            testThresholds = null,
            deviceName = "Pixel",
            deviceSerialNumber = "456",
            platform = "android",
            appName = "App",
            appVersion = "1.0",
            appPackage = "pkg",
            mainActivity = "main"
        )
        every { androidRunner.run(config) } returns mapOf("output2" to "value2")
        val result = testRunner.run(config)
        assertEquals(mapOf("output2" to "value2"), result)
        verify(exactly = 1) { androidRunner.run(config) }
        verify(exactly = 0) { iosRunner.run(any()) }
    }

    @Test
    fun `run returns empty map for unknown platform`() {
        val config = TestExecutionConfigDTO(
            executionTypeName = "type",
            metricName = "metric",
            metricParams = emptyMap(),
            executionTypeParams = emptyMap(),
            testThresholds = null,
            deviceName = "Unknown",
            deviceSerialNumber = "000",
            platform = "windows",
            appName = "App",
            appVersion = "1.0",
            appPackage = "pkg",
            mainActivity = null
        )
        val result = testRunner.run(config)
        assertTrue(result.isEmpty())
        verify(exactly = 0) { iosRunner.run(any()) }
        verify(exactly = 0) { androidRunner.run(any()) }
    }
}