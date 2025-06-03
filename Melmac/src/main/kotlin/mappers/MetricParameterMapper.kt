package mappers

import domain.MetricParameter
import dtos.MetricParameterResponseDTO

object MetricParameterMapper {
    fun toDto(metricParameter: MetricParameter): MetricParameterResponseDTO {
        return MetricParameterResponseDTO(
            metricParameterId = metricParameter.metricParameterId
                ?: throw IllegalStateException("MetricParameter ID cannot be null"),
            parameterName = metricParameter.parameterName,
            parameterType = metricParameter.parameterType,
            metricMetricId = metricParameter.metricMetricId
        )
    }

    fun toDomain(dto: MetricParameterResponseDTO): MetricParameter {
        return MetricParameter(
            metricParameterId = dto.metricParameterId,
            parameterName = dto.parameterName,
            parameterType = dto.parameterType,
            metricMetricId = dto.metricMetricId
        )
    }
}