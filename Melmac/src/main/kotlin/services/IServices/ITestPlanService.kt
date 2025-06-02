package services.IServices
import dtos.*

/**
 * Service interface for managing test plans.
 * Provides methods to retrieve a test plan by ID and create a new test plan with versioning.
 */
interface ITestPlanService {
    fun getTestPlanById(id: Int): TestPlanResponseDTO?
    fun createTestPlanWithVersion(request: TestPlanVersionRequestDTO): TestPlanVersionResponseDTO
}