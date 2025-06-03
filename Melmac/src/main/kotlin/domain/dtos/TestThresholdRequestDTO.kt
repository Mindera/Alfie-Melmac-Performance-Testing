package dtos

data class TestThresholdRequestDTO(
    val targetValue: Int,
    val thresholdType: String,
    val testPlanVersionTestPlanVersionId: Int,
    val metricOutputMetricOutputId: Int
)
