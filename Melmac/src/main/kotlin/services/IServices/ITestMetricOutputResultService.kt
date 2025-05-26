package services.IServices

import dtos.TestMetricOutputResultResponseDTO

interface ITestMetricOutputResultService {
    suspend fun getAll(): List<TestMetricOutputResultResponseDTO>
    suspend fun getByExecutionId(testExecutionId: Int): List<TestMetricOutputResultResponseDTO>
}
