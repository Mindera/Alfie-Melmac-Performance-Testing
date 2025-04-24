package domain.dtos

data class ExecutionTypeParameterResponseDTO(
    val id: Int,
    val name: String,
    val type: String,
    val executionTypeId: Int
)
