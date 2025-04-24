package domain.dtos

data class OutputResponseDTO(
    val id: Int,
    val metricId: Int,
    val name: String,
    val unit: String,
)