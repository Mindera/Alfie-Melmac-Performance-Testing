package dtos

data class MetricOutputResponseDTO(
    val metricOutputId: Int,
    val outputName: String?,
    val unit: String,
    val metricMetricId: Int
)
