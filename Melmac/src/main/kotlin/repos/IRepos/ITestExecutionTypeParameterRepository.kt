package repos.IRepos

import domain.TestExecutionTypeParameter

interface ITestExecutionTypeParameterRepository {
    fun save(param: TestExecutionTypeParameter): TestExecutionTypeParameter
    fun saveAll(params: List<TestExecutionTypeParameter>)
    fun findByExecutionId(executionId: Int): List<TestExecutionTypeParameter>
}
