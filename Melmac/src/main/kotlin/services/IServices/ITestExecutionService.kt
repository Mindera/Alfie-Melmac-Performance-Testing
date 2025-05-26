package services.IServices

import dtos.TestExecutionResponseDTO

interface ITestExecutionService {
    fun getAllTestExecutions(): List<TestExecutionResponseDTO>
    fun getTestExecutionById(id: Int): TestExecutionResponseDTO?
    fun runTestExecution(testPlanVersionId: Int): TestExecutionResponseDTO
}
