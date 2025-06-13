import mappers.MetricMapper
import domain.Metric
import dtos.MetricResponseDTO
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MetricMapperTest {
    @Test
    fun `toDto maps Metric to MetricResponseDTO correctly`() {
        val metric = Metric(1, "Throughput")
        val dto = MetricMapper.toDto(metric)
        assertEquals(1, dto.metricId)
        assertEquals("Throughput", dto.metricName)
    }

    @Test
    fun `toDto throws if metricId is null`() {
        val metric = Metric(null, "Latency")
        assertThrows(IllegalStateException::class.java) {
            MetricMapper.toDto(metric)
        }
    }

    @Test
    fun `toDomain maps MetricResponseDTO to Metric correctly`() {
        val dto = MetricResponseDTO(2, "Latency")
        val metric = MetricMapper.toDomain(dto)
        assertEquals(2, metric.metricId)
        assertEquals("Latency", metric.metricName)
    }
}