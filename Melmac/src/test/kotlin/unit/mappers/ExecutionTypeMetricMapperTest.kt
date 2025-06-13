import mappers.ExecutionTypeMetricMapper
import domain.ExecutionTypeMetric
import dtos.ExecutionTypeMetricResponseDTO
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ExecutionTypeMetricMapperTest {
    @Test
    fun `toDto maps ExecutionTypeMetric to ExecutionTypeMetricResponseDTO correctly`() {
        val etm = ExecutionTypeMetric(1, 2)
        val dto = ExecutionTypeMetricMapper.toDto(etm)
        assertEquals(1, dto.executionTypeExecutionTypeId)
        assertEquals(2, dto.metricMetricId)
    }

    @Test
    fun `toDomain maps ExecutionTypeMetricResponseDTO to ExecutionTypeMetric correctly`() {
        val dto = ExecutionTypeMetricResponseDTO(3, 4)
        val etm = ExecutionTypeMetricMapper.toDomain(dto)
        assertEquals(3, etm.executionTypeExecutionTypeId)
        assertEquals(4, etm.metricMetricId)
    }
}