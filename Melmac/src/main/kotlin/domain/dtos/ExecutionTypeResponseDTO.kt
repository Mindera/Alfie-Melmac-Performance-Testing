package domain.dtos

data class ExecutionTypeResponseDTO(
    val id: Int,
    val name: String,
    val description: String? = null,
)