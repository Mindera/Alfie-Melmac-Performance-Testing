package dtos

data class TestMetricOutputResultResponseDTO(
    val testMetricOutputResultId: Int,
    val value: String,
    val metricOutputMetricOutputId: Int,
    val testExecutionTestExecutionId: Int
)
