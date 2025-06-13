import mappers.TestMetricParameterMapper
import domain.TestMetricParameter
import dtos.TestMetricParameterResponseDTO
import dtos.TestMetricParameterRequestDTO
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TestMetricParameterMapperTest {
    @Test
    fun `toDto maps TestMetricParameter to TestMetricParameterResponseDTO correctly`() {
        val param = TestMetricParameter(1, "val", 2, 3)
        val dto = TestMetricParameterMapper.toDto(param)
        assertEquals(1, dto.testMetricParameterId)
        assertEquals("val", dto.parameterValue)
        assertEquals(2, dto.metricParameterMetricParameterId)
        assertEquals(3, dto.testPlanVersionTestPlanVersionId)
    }

    @Test
    fun `toDto throws if testMetricParameterId is null`() {
        val param = TestMetricParameter(null, "val", 2, 3)
        assertThrows(IllegalStateException::class.java) {
            TestMetricParameterMapper.toDto(param)
        }
    }

    @Test
    fun `toDomain maps TestMetricParameterResponseDTO to TestMetricParameter correctly`() {
        val dto = TestMetricParameterResponseDTO(4, "foo", 5, 6)
        val param = TestMetricParameterMapper.toDomain(dto)
        assertEquals(4, param.testMetricParameterId)
        assertEquals("foo", param.parameterValue)
        assertEquals(5, param.metricParameterMetricParameterId)
        assertEquals(6, param.testPlanVersionTestPlanVersionId)
    }

    @Test
    fun `fromRequestDto maps request DTO and ids to TestMetricParameter correctly`() {
        val req = TestMetricParameterRequestDTO("bar", "8", 7)
        val param = TestMetricParameterMapper.fromRequestDto(req, testPlanVersionId = 7, metricParameterId = 8)
        assertNull(param.testMetricParameterId)
        assertEquals("bar", param.parameterValue)
        assertEquals(8, param.metricParameterMetricParameterId)
        assertEquals(7, param.testPlanVersionTestPlanVersionId)
    }
}