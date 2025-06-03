package mappers

import domain.MetricOutput
import dtos.MetricOutputResponseDTO

object MetricOutputMapper {
    fun toDto(metricOutput: MetricOutput): MetricOutputResponseDTO {
        return MetricOutputResponseDTO(
            metricOutputId = metricOutput.metricOutputId ?: throw IllegalStateException("MetricOutput ID cannot be null"),
            outputName = metricOutput.outputName,
            unit = metricOutput.unit,
            metricMetricId = metricOutput.metricMetricId
        )
    }

    fun toDomain(dto: MetricOutputResponseDTO): MetricOutput {
        return MetricOutput(
            metricOutputId = dto.metricOutputId,
            outputName = dto.outputName,
            unit = dto.unit,
            metricMetricId = dto.metricMetricId
        )
    }
}