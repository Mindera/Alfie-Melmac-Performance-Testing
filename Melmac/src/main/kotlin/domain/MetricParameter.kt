package domain

data class MetricParameter(
    val metricParameterId: Int? = null,
    val parameterName: String,
    val parameterType: String,
    val metricMetricId: Int
)
