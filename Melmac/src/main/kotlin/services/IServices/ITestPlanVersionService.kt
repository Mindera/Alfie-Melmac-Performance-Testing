package services.IServices
import dtos.TestPlanVersionResponseDTO

interface ITestPlanVersionService {
    fun getTestPlanVersionsByTestPlanId(id: Int): List<TestPlanVersionResponseDTO>
    fun getLatestTestPlanVersionByTestPlanId(testPlanId: Int): TestPlanVersionResponseDTO?
    fun getTestPlanVersionById(id: Int): TestPlanVersionResponseDTO?
}