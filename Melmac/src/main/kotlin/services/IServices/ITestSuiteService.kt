package services.IServices

import dtos.*

/**
 * Service interface for managing metrics and their associated data.
 * Provides methods to retrieve metric information, including outputs, parameters, and execution types.
 */
interface ITestSuiteService {
    fun createTestSuite(request: TestSuiteRequestDTO): TestSuiteResponseDTO
    fun getAllTestSuites(): List<TestSuiteResponseDTO>
    fun getTestSuiteById(id: Int): TestSuiteResponseDTO?
    fun getTestPlanVersionsBySuiteId(suiteId: Int): List<TestPlanVersionResponseDTO>
    fun runTestSuiteExecution(suiteId: Int): SuiteExecutionResponseDTO
}
