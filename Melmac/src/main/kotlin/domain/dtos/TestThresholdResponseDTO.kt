package dtos

data class TestThresholdResponseDTO(
    val testThresholdId: Int,
    val targetValue: Int,
    val thresholdTypeThresholdTypeId: Int,
    val testPlanVersionTestPlanVersionId: Int,
    val metricOutputMetricOutputId: Int
)
