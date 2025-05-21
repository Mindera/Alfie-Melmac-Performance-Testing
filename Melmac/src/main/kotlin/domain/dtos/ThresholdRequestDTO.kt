package domain.dtos

data class ThresholdRequestDTO(
    val testMetricId: Int,
    val thresholdTypeId: Int,
    val value: Double
)