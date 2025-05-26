package dtos

data class TestMetricParameterRequestDTO(
    val parameterValue: String,
    val metricParameterMetricParameterId: Int,
    val testPlanVersionTestPlanVersionId: Int
)
