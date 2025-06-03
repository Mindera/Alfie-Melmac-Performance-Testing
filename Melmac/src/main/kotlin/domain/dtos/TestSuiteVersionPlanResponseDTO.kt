package dtos

data class TestSuiteVersionPlanResponseDTO(
    val testSuiteVersionTestSuiteVersionId: Int,
    val testPlanVersionTestPlanVersionId: Int,
    val order: Int
)
