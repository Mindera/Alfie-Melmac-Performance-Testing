package domain

data class ExecutionTypeParameter(
    val id: Int? = null,
    val name: String,
    val type: String,
    val executionTypeId: Int
)
