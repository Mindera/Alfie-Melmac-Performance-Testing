package services.IServices

import dtos.*

/**
 * Service interface for managing metrics and their associated data.
 * Provides methods to retrieve metric information, including outputs, parameters, and execution types.
 */
interface IThresholdService {
    fun getThresholdByTestPlanVersionId(testPlanVersionId: Int): List<TestThresholdResponseDTO>
    fun getThresholdById(id: Int): TestThresholdResponseDTO?
    fun createTestThreshold(request: TestThresholdRequestDTO): TestThresholdResponseDTO
}
