import mappers.TestMetricOutputResultMapper
import domain.TestMetricOutputResult
import dtos.TestMetricOutputResultResponseDTO
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TestMetricOutputResultMapperTest {
    @Test
    fun `toDto maps TestMetricOutputResult to TestMetricOutputResultResponseDTO correctly`() {
        val result = TestMetricOutputResult(1, "123.45", 2, 3)
        val dto = TestMetricOutputResultMapper.toDto(result)
        assertEquals(1, dto.testMetricOutputResultId)
        assertEquals("123.45", dto.value)
        assertEquals(2, dto.metricOutputMetricOutputId)
        assertEquals(3, dto.testExecutionTestExecutionId)
    }

    @Test
    fun `toDto throws if testMetricOutputResultId is null`() {
        val result = TestMetricOutputResult(null, "123.45", 2, 3)
        assertThrows(IllegalStateException::class.java) {
            TestMetricOutputResultMapper.toDto(result)
        }
    }

    @Test
    fun `toDomain maps TestMetricOutputResultResponseDTO to TestMetricOutputResult correctly`() {
        val dto = TestMetricOutputResultResponseDTO(4, "67.89", 5, 6)
        val result = TestMetricOutputResultMapper.toDomain(dto)
        assertEquals(4, result.testMetricOutputResultId)
        assertEquals("67.89", result.value)
        assertEquals(5, result.metricOutputMetricOutputId)
        assertEquals(6, result.testExecutionTestExecutionId)
    }
}