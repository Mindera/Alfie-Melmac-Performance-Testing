package domain

data class TestThreshold(
    val testThresholdId: Int? = null,
    val targetValue: Int,
    val thresholdTypeThresholdTypeId: Int,
    val testPlanVersionTestPlanVersionId: Int,
    val metricOutputMetricOutputId: Int
)
