package dtos

data class ExecutionTypeResponseDTO(
    val executionTypeId: Int,
    val executionTypeName: String,
    val executionTypeDescription: String?
)
