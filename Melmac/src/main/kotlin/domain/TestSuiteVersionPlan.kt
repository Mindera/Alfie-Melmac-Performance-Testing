package domain

data class TestSuiteVersionPlan(
    val testSuiteVersionTestSuiteVersionId: Int,
    val testPlanVersionTestPlanVersionId: Int,
    val order: Int
)
