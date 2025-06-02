package repos.IRepos

import domain.TestExecutionTypeParameter

/**
 * Repository interface for managing TestExecutionTypeParameter entities.
 */
interface ITestPlanExecutionTypeParameterValueRepository {
    fun findByTestPlanVersionId(testPlanVersionId: Int): List<TestExecutionTypeParameter>
    fun save(testExecutionTypeParameter: TestExecutionTypeParameter): Int
}