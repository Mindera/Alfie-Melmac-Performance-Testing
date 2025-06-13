import mappers.TestThresholdMapper
import domain.TestThreshold
import dtos.TestThresholdResponseDTO
import dtos.TestThresholdRequestDTO
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TestThresholdMapperTest {
    @Test
    fun `toDto maps TestThreshold to TestThresholdResponseDTO correctly`() {
        val threshold = TestThreshold(1, 100, 2, 3, 4)
        val dto = TestThresholdMapper.toDto(threshold)
        assertEquals(1, dto.testThresholdId)
        assertEquals(100, dto.targetValue)
        assertEquals(2, dto.thresholdTypeThresholdTypeId)
        assertEquals(3, dto.testPlanVersionTestPlanVersionId)
        assertEquals(4, dto.metricOutputMetricOutputId)
    }

    @Test
    fun `toDto throws if testThresholdId is null`() {
        val threshold = TestThreshold(null, 100, 2, 3, 4)
        assertThrows(IllegalStateException::class.java) {
            TestThresholdMapper.toDto(threshold)
        }
    }

    @Test
    fun `toDomain maps TestThresholdResponseDTO to TestThreshold correctly`() {
        val dto = TestThresholdResponseDTO(5, 200, 6, 7, 8)
        val threshold = TestThresholdMapper.toDomain(dto)
        assertEquals(5, threshold.testThresholdId)
        assertEquals(200, threshold.targetValue)
        assertEquals(6, threshold.thresholdTypeThresholdTypeId)
        assertEquals(7, threshold.testPlanVersionTestPlanVersionId)
        assertEquals(8, threshold.metricOutputMetricOutputId)
    }

    @Test
    fun `fromRequestDto maps request DTO and ids to TestThreshold correctly`() {
        val req = TestThresholdRequestDTO(300, "11", 10, 9)
        val thresholdTypeId = 11
        val testPlanVersionId = 10
        val threshold = TestThresholdMapper.fromRequestDto(req, testPlanVersionId, thresholdTypeId) 
        assertNull(threshold.testThresholdId)
        assertEquals(300, threshold.targetValue)
        assertEquals(11, threshold.thresholdTypeThresholdTypeId)
        assertEquals(10, threshold.testPlanVersionTestPlanVersionId)
        assertEquals(9, threshold.metricOutputMetricOutputId)
    }
}