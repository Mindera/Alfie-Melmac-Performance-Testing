package services.IServices
import dtos.TestPlanVersionResponseDTO

/**
 * Service interface for managing test plan versions.
 * Provides methods to retrieve test plan versions by test plan ID, get the latest version,
 * and retrieve a specific version by its ID.
 */
interface ITestPlanVersionService {
    fun getTestPlanVersionsByTestPlanId(id: Int): List<TestPlanVersionResponseDTO>
    fun getLatestTestPlanVersionByTestPlanId(testPlanId: Int): TestPlanVersionResponseDTO?
    fun getTestPlanVersionById(id: Int): TestPlanVersionResponseDTO?
}