package domain

data class TestExecutionTypeParameter(
    val id: Int,
    val executionId: Int,
    val executionTypeParameterId: Int,
    val value: String
)