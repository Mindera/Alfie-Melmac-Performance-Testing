package domain.dtos

data class MetricResponseDTO(
    val id: Int,
    val name: String,
    val description: String? = null,
)