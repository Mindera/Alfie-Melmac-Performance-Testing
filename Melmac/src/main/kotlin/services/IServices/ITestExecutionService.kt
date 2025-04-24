package services.IServices

import domain.dtos.TestExecutionRequestDTO
import domain.dtos.TestExecutionResponseDTO

interface ITestExecutionService {
    fun create(request: TestExecutionRequestDTO): TestExecutionResponseDTO
    fun listAll(): List<TestExecutionResponseDTO>
    fun listBySuiteId(suiteId: Int): List<TestExecutionResponseDTO>
}

