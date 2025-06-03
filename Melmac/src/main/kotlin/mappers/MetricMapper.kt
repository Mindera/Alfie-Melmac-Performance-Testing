package mappers

import domain.Metric
import dtos.MetricResponseDTO

object MetricMapper {
    fun toDto(metric: Metric): MetricResponseDTO {
        return MetricResponseDTO(
            metricId = metric.metricId ?: throw IllegalStateException("Metric ID cannot be null"),
            metricName = metric.metricName
        )
    }

    fun toDomain(dto: MetricResponseDTO): Metric {
        return Metric(
            metricId = dto.metricId,
            metricName = dto.metricName
        )
    }
}