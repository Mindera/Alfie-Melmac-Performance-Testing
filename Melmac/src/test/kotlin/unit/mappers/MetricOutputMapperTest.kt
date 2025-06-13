import mappers.MetricOutputMapper
import domain.MetricOutput
import dtos.MetricOutputResponseDTO
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MetricOutputMapperTest {
    @Test
    fun `toDto maps MetricOutput to MetricOutputResponseDTO correctly`() {
        val metricOutput = MetricOutput(1, "output", "ms", 2)
        val dto = MetricOutputMapper.toDto(metricOutput)
        assertEquals(1, dto.metricOutputId)
        assertEquals("output", dto.outputName)
        assertEquals("ms", dto.unit)
        assertEquals(2, dto.metricMetricId)
    }

    @Test
    fun `toDto throws if metricOutputId is null`() {
        val metricOutput = MetricOutput(null, "output", "ms", 2)
        assertThrows(IllegalStateException::class.java) {
            MetricOutputMapper.toDto(metricOutput)
        }
    }

    @Test
    fun `toDomain maps MetricOutputResponseDTO to MetricOutput correctly`() {
        val dto = MetricOutputResponseDTO(3, "latency", "s", 4)
        val metricOutput = MetricOutputMapper.toDomain(dto)
        assertEquals(3, metricOutput.metricOutputId)
        assertEquals("latency", metricOutput.outputName)
        assertEquals("s", metricOutput.unit)
        assertEquals(4, metricOutput.metricMetricId)
    }
}