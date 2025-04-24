package domain

data class Threshold(
    val id: Int? = null,
    val testMetricId: Int,
    val thresholdTypeId: Int,
    val value: Double
)
