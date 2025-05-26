package services.IServices

import dtos.*

interface ITestSuiteService {
    fun createTestSuite(request: TestSuiteRequestDTO): TestSuiteResponseDTO
    fun getAllTestSuites(): List<TestSuiteResponseDTO>
    fun getTestSuiteById(id: Int): TestSuiteResponseDTO?
    fun getTestPlanVersionsBySuiteId(suiteId: Int): List<TestPlanVersionResponseDTO>
    fun runTestSuiteExecution(suiteId: Int): SuiteExecutionResponseDTO
}
