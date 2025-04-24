package services.IServices

import domain.dtos.TestMetricRequestDTO
import domain.dtos.TestMetricResponseDTO

interface ITestMetricService {
    suspend fun getAll(): List<TestMetricResponseDTO>
    suspend fun getByExecutionId(testExecutionId: Int): List<TestMetricResponseDTO>
    suspend fun create(dto: TestMetricRequestDTO): TestMetricResponseDTO
    suspend fun delete(id: Int)
}
