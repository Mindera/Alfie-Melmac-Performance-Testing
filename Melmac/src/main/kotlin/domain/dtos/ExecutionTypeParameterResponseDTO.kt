package dtos

data class ExecutionTypeParameterResponseDTO(
    val executionTypeParameterId: Int,
    val parameterName: String,
    val parameterType: String,
    val executionTypeExecutionTypeId: Int
)
