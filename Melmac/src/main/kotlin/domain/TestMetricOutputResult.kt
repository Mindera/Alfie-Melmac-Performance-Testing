package domain

data class TestMetricOutputResult(
    val testMetricOutputResultId: Int? = null,
    val value: String,
    val metricOutputMetricOutputId: Int,
    val testExecutionTestExecutionId: Int
)
