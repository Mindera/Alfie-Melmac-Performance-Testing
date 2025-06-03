package dtos

data class TestExecutionTypeParameterRequestDTO(
    val parameterValue: String,
    val executionTypeParameterExecutionTypeParameterId: Int,
    val testPlanVersionTestPlanVersionId: Int
)
