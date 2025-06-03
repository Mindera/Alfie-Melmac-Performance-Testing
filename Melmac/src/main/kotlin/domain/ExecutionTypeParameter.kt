package domain

data class ExecutionTypeParameter(
    val executionTypeParameterId: Int? = null,
    val parameterName: String,
    val parameterType: String,
    val executionTypeExecutionTypeId: Int
)
