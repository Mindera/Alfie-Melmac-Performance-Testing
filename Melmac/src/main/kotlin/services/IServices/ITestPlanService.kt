package services.IServices
import dtos.*

interface ITestPlanService {
    fun getTestPlanById(id: Int): TestPlanResponseDTO?
    fun createTestPlanWithVersion(request: TestPlanVersionRequestDTO): TestPlanVersionResponseDTO
}