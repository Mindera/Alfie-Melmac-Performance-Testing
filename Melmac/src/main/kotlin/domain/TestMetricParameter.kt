package domain

data class TestMetricParameter(
    val testMetricParameterId: Int? = null,
    val parameterValue: String,
    val metricParameterMetricParameterId: Int,
    val testPlanVersionTestPlanVersionId: Int
)
