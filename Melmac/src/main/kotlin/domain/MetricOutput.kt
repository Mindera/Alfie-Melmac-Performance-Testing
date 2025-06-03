package domain

data class MetricOutput(
    val metricOutputId: Int? = null,
    val outputName: String?,
    val unit: String,
    val metricMetricId: Int
)
