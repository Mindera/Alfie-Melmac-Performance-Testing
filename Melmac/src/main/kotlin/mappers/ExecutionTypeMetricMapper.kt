package mappers

import domain.ExecutionTypeMetric
import dtos.ExecutionTypeMetricResponseDTO

object ExecutionTypeMetricMapper {
    fun toDto(executionTypeMetric: ExecutionTypeMetric): ExecutionTypeMetricResponseDTO {
        return ExecutionTypeMetricResponseDTO(
            executionTypeExecutionTypeId = executionTypeMetric.executionTypeExecutionTypeId,
            metricMetricId = executionTypeMetric.metricMetricId
        )
    }

    fun toDomain(dto: ExecutionTypeMetricResponseDTO): ExecutionTypeMetric {
        return ExecutionTypeMetric(
            executionTypeExecutionTypeId = dto.executionTypeExecutionTypeId,
            metricMetricId = dto.metricMetricId
        )
    }
}