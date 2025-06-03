package dtos

data class TestThresholdRequestDTO(
    val targetValue: Int,
    val thresholdTypeThresholdTypeId: Int,
    val testPlanVersionTestPlanVersionId: Int,
    val metricOutputMetricOutputId: Int
)
