package dtos

data class TestMetricParameterResponseDTO(
    val testMetricParameterId: Int,
    val parameterValue: String,
    val metricParameterMetricParameterId: Int,
    val testPlanVersionTestPlanVersionId: Int
)

