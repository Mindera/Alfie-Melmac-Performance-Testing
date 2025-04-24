package services.IServices

import domain.dtos.TestSuiteRequestDTO
import domain.dtos.TestSuiteResponseDTO

interface ITestSuiteService {
    fun create(request: TestSuiteRequestDTO): TestSuiteResponseDTO
    fun listAll(): List<TestSuiteResponseDTO>
    fun findById(id: Int): TestSuiteResponseDTO?
}
