package repos.IRepos

import domain.TestExecutionTypeParameter

interface ITestPlanExecutionTypeParameterValueRepository {
    fun findByTestPlanVersionId(testPlanVersionId: Int): List<TestExecutionTypeParameter>
    fun save(testExecutionTypeParameter: TestExecutionTypeParameter): Int
}