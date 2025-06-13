import mappers.TestPlanVersionMapper
import domain.TestPlanVersion
import dtos.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class TestPlanVersionMapperTest {
    @Test
    fun `toDto maps TestPlanVersion and lists to TestPlanVersionResponseDTO correctly`() {
        val testPlanVersion = TestPlanVersion(
            testPlanVersionId = 1,
            version = "v1",
            creationTimestamp = LocalDateTime.MIN,
            notes = "notes",
            appPackage = "pkg",
            mainActivity = "main",
            testPlanTestPlanId = 2,
            deviceDeviceId = 3,
            appVersionAppVersionId = 4,
            executionTypeExecutionTypeId = 5
        )
        val thresholds = listOf(TestThresholdResponseDTO(10, 100, 1, 2, 3))
        val metricParameters = listOf(TestMetricParameterResponseDTO(20, "val", 21, 22))
        val executionTypeParameters = listOf(TestExecutionTypeParameterResponseDTO(30, "val2", 31, 32))
        val testSuiteVersionId = 99

        val dto = TestPlanVersionMapper.toDto(
            testPlanVersion,
            thresholds,
            metricParameters,
            executionTypeParameters,
            testSuiteVersionId
        )
        assertEquals(1, dto.testPlanVersionId)
        assertEquals("v1", dto.version)
        assertEquals(LocalDateTime.MIN, dto.creationTimestamp)
        assertEquals("notes", dto.notes)
        assertEquals("pkg", dto.appPackage)
        assertEquals("main", dto.mainActivity)
        assertEquals(2, dto.testPlanTestPlanId)
        assertEquals(3, dto.deviceDeviceId)
        assertEquals(4, dto.appVersionAppVersionId)
        assertEquals(5, dto.executionTypeExecutionTypeId)
        assertEquals(thresholds, dto.thresholds)
        assertEquals(metricParameters, dto.metricParameters)
        assertEquals(executionTypeParameters, dto.executionTypeParameters)
        assertEquals(99, dto.testSuiteVersionId)
    }

    @Test
    fun `toDto throws if testPlanVersionId is null`() {
        val testPlanVersion = TestPlanVersion(
            testPlanVersionId = null,
            version = "v1",
            creationTimestamp = LocalDateTime.MIN,
            notes = "notes",
            appPackage = "pkg",
            mainActivity = "main",
            testPlanTestPlanId = 2,
            deviceDeviceId = 3,
            appVersionAppVersionId = 4,
            executionTypeExecutionTypeId = 5
        )
        assertThrows(IllegalStateException::class.java) {
            TestPlanVersionMapper.toDto(
                testPlanVersion,
                emptyList(),
                emptyList(),
                emptyList(),
                0
            )
        }
    }

    @Test
    fun `toDomain maps TestPlanVersionResponseDTO to TestPlanVersion correctly`() {
        val dto = TestPlanVersionResponseDTO(
            testPlanVersionId = 7,
            version = "v2",
            creationTimestamp = LocalDateTime.MAX,
            notes = "n2",
            appPackage = "pkg2",
            mainActivity = "main2",
            testPlanTestPlanId = 8,
            deviceDeviceId = 9,
            appVersionAppVersionId = 10,
            executionTypeExecutionTypeId = 11,
            thresholds = emptyList(),
            metricParameters = emptyList(),
            executionTypeParameters = emptyList(),
            testSuiteVersionId = 12
        )
        val testPlanVersion = TestPlanVersionMapper.toDomain(dto)
        assertEquals(7, testPlanVersion.testPlanVersionId)
        assertEquals("v2", testPlanVersion.version)
        assertEquals(LocalDateTime.MAX, testPlanVersion.creationTimestamp)
        assertEquals("n2", testPlanVersion.notes)
        assertEquals("pkg2", testPlanVersion.appPackage)
        assertEquals("main2", testPlanVersion.mainActivity)
        assertEquals(8, testPlanVersion.testPlanTestPlanId)
        assertEquals(9, testPlanVersion.deviceDeviceId)
        assertEquals(10, testPlanVersion.appVersionAppVersionId)
        assertEquals(11, testPlanVersion.executionTypeExecutionTypeId)
    }
}