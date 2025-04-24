package domain

data class MetricParameter(
    val id: Int? = null,
    val name: String,
    val type: String,
    val metricId: Int
)