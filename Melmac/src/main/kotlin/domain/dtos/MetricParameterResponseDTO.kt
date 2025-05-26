package dtos

data class MetricParameterResponseDTO(
    val metricParameterId: Int,
    val parameterName: String,
    val parameterType: String,
    val metricMetricId: Int
)
