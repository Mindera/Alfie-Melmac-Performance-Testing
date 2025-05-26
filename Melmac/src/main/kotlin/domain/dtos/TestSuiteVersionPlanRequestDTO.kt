package dtos

data class TestSuiteVersionPlanRequestDTO(
    val testSuiteVersionTestSuiteVersionId: Int,
    val testPlanVersionTestPlanVersionId: Int,
    val order: Int
)
