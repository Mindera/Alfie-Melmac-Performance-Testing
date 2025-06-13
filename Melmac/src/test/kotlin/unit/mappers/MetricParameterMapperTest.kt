import mappers.MetricParameterMapper
import domain.MetricParameter
import dtos.MetricParameterResponseDTO
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MetricParameterMapperTest {
    @Test
    fun `toDto maps MetricParameter to MetricParameterResponseDTO correctly`() {
        val param = MetricParameter(1, "window", "int", 2)
        val dto = MetricParameterMapper.toDto(param)
        assertEquals(1, dto.metricParameterId)
        assertEquals("window", dto.parameterName)
        assertEquals("int", dto.parameterType)
        assertEquals(2, dto.metricMetricId)
    }

    @Test
    fun `toDto throws if metricParameterId is null`() {
        val param = MetricParameter(null, "window", "int", 2)
        assertThrows(IllegalStateException::class.java) {
            MetricParameterMapper.toDto(param)
        }
    }

    @Test
    fun `toDomain maps MetricParameterResponseDTO to MetricParameter correctly`() {
        val dto = MetricParameterResponseDTO(3, "interval", "long", 4)
        val param = MetricParameterMapper.toDomain(dto)
        assertEquals(3, param.metricParameterId)
        assertEquals("interval", param.parameterName)
        assertEquals("long", param.parameterType)
        assertEquals(4, param.metricMetricId)
    }
}