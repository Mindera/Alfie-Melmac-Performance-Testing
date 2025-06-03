package dtos

data class TestExecutionTypeParameterRequestDTO(
    val parameterValue: String,
    val executionTypeParameter: String,
    val testPlanVersionTestPlanVersionId: Int
)
