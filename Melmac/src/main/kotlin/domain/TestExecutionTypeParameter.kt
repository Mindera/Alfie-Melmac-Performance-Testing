package domain

data class TestExecutionTypeParameter(
    val testExecutionTypeParameterId: Int? = null,
    val parameterValue: String,
    val executionTypeParameterExecutionTypeParameterId: Int,
    val testPlanVersionTestPlanVersionId: Int
)
