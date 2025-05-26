package services.IServices

import dtos.*

interface IThresholdService {
    fun getThresholdByTestPlanVersionId(testPlanVersionId: Int): List<TestThresholdResponseDTO>
    fun getThresholdById(id: Int): TestThresholdResponseDTO?
    fun createTestThreshold(request: TestThresholdRequestDTO): TestThresholdResponseDTO
}
