package services.IServices

import dtos.TestExecutionResponseDTO

/**
 * Service interface for managing test executions.
 * Provides methods to retrieve and run test executions.
 */
interface ITestExecutionService {
    fun getAllTestExecutions(): List<TestExecutionResponseDTO>
    fun getTestExecutionById(id: Int): TestExecutionResponseDTO?
    fun runTestExecution(testPlanVersionId: Int): TestExecutionResponseDTO
}
