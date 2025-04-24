package domain

data class TestMetricParameter(
    val id: Int,
    val testExecutionId: Int,
    val metricParameterId: Int,
    val value: String
)