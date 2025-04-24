package domain.dtos

data class MetricParameterResponseDTO(
    val id: Int,
    val name: String,
    val type: String,
    val metricId: Int
)
