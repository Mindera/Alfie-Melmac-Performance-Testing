package dtos

data class ThresholdTypeResponseDTO(
    val thresholdTypeId: Int,
    val thresholdTypeName: String,
    val thresholdTypeDescription: String?
)
