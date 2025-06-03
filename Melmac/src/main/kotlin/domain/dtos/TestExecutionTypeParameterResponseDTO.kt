package dtos

data class TestExecutionTypeParameterResponseDTO(
    val testExecutionTypeParameterId: Int,
    val parameterValue: String,
    val executionTypeParameterExecutionTypeParameterId: Int,
    val testPlanVersionTestPlanVersionId: Int
)
