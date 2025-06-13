import mappers.TestExecutionMapper
import domain.TestExecution
import dtos.TestExecutionResponseDTO
import dtos.TestExecutionConfigDTO
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class TestExecutionMapperTest {
    @Test
    fun `toDto maps TestExecution to TestExecutionResponseDTO correctly`() {
        val exec = TestExecution(1, LocalDateTime.MIN, LocalDateTime.MAX, "true", 42)
        val dto = TestExecutionMapper.toDto(exec)
        assertEquals(1, dto.testExecutionId)
        assertEquals(LocalDateTime.MIN, dto.initialTimestamp)
        assertEquals(LocalDateTime.MAX, dto.endTimestamp)
        assertEquals("true", dto.passed)
        assertEquals(42, dto.testPlanVersionTestPlanVersionId)
    }

    @Test
    fun `toDto throws if testExecutionId is null`() {
        val exec = TestExecution(null, LocalDateTime.MIN, LocalDateTime.MAX, "true", 42)
        assertThrows(IllegalStateException::class.java) {
            TestExecutionMapper.toDto(exec)
        }
    }

    @Test
    fun `toDomain maps TestExecutionResponseDTO to TestExecution correctly`() {
        val dto = TestExecutionResponseDTO(5, LocalDateTime.MIN, LocalDateTime.MAX, "false", 99)
        val exec = TestExecutionMapper.toDomain(dto)
        assertEquals(5, exec.testExecutionId)
        assertEquals(LocalDateTime.MIN, exec.initialTimestamp)
        assertEquals(LocalDateTime.MAX, exec.endTimestamp)
        assertEquals("false", exec.passed)
        assertEquals(99, exec.testPlanVersionTestPlanVersionId)
    }

    @Test
    fun `toConfigDto maps parameters to TestExecutionConfigDTO correctly`() {
        val config = TestExecutionMapper.toConfigDto(
            executionTypeName = "type",
            metricName = "metric",
            metricParams = mapOf("foo" to "bar"),
            executionTypeParams = mapOf("baz" to "qux"),
            testThresholds = listOf(Triple("a", "b", "c")),
            deviceName = "Pixel",
            deviceSerialNumber = "123",
            platform = "Android",
            appName = "App",
            appVersion = "1.0",
            appPackage = "pkg",
            mainActivity = "main"
        )
        assertEquals("type", config.executionTypeName)
        assertEquals("metric", config.metricName)
        assertEquals(mapOf("foo" to "bar"), config.metricParams)
        assertEquals(mapOf("baz" to "qux"), config.executionTypeParams)
        assertEquals(listOf(Triple("a", "b", "c")), config.testThresholds)
        assertEquals("Pixel", config.deviceName)
        assertEquals("123", config.deviceSerialNumber)
        assertEquals("Android", config.platform)
        assertEquals("App", config.appName)
        assertEquals("1.0", config.appVersion)
        assertEquals("pkg", config.appPackage)
        assertEquals("main", config.mainActivity)
    }
}