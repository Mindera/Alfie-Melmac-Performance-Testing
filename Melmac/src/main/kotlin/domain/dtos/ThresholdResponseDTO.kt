package domain.dtos

data class ThresholdResponseDTO(
    val id: Int,
    val testMetricId: Int,
    val thresholdTypeId: Int,
    val value: Double
)