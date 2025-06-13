package services.IServices

import dtos.TestMetricOutputResultResponseDTO

/**
 * Service interface for managing test metric output results.
 * Provides methods to retrieve test metric output results by execution ID or all results.
 */
interface ITestMetricOutputResultService {
    fun getAll(): List<TestMetricOutputResultResponseDTO>
    fun getByExecutionId(testExecutionId: Int): List<TestMetricOutputResultResponseDTO>
}
