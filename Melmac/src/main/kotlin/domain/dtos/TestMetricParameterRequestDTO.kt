package dtos

data class TestMetricParameterRequestDTO(
    val parameterValue: String,
    val metricParameter: String,
    val testPlanVersionTestPlanVersionId: Int
)
