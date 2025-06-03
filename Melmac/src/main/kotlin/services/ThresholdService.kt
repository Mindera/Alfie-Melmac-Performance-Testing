package services

import domain.*
import dtos.*
import repos.IRepos.IThresholdRepository
import services.IServices.IThresholdService
import java.time.LocalDateTime

/**
 * Service implementation for managing Test Thresholds.
 *
 * Provides methods to retrieve and create test thresholds associated with test plan versions.
 *
 * @property thresholdRepository Repository for TestThreshold entities.
 */
class ThresholdService(
    private val thresholdRepository: IThresholdRepository
) : IThresholdService {

    /**
     * Retrieves all thresholds for a given test plan version ID.
     *
     * @param testPlanVersionId The ID of the test plan version.
     * @return List of [TestThresholdResponseDTO] representing each threshold.
     */
    override fun getThresholdByTestPlanVersionId(testPlanVersionId: Int): List<TestThresholdResponseDTO> {
        val thresholds = thresholdRepository.findByTestPlanVersionId(testPlanVersionId)
        return thresholds.map { threshold ->
            TestThresholdResponseDTO(
                testThresholdId = threshold.testThresholdId ?: 0,
                thresholdTypeThresholdTypeId = threshold.thresholdTypeThresholdTypeId,
                targetValue = threshold.targetValue,
                metricOutputMetricOutputId = threshold.metricOutputMetricOutputId,
                testPlanVersionTestPlanVersionId = threshold.testPlanVersionTestPlanVersionId
            )
        }
    }

    /**
     * Retrieves a threshold by its ID.
     *
     * @param id The ID of the threshold.
     * @return [TestThresholdResponseDTO] for the specified threshold, or null if not found.
     */
    override fun getThresholdById(id: Int): TestThresholdResponseDTO? {
        val threshold = thresholdRepository.findById(id) ?: return null
        return TestThresholdResponseDTO(
            testThresholdId = threshold.testThresholdId ?: 0,
            thresholdTypeThresholdTypeId = threshold.thresholdTypeThresholdTypeId,
            targetValue = threshold.targetValue,
            metricOutputMetricOutputId = threshold.metricOutputMetricOutputId,
            testPlanVersionTestPlanVersionId = threshold.testPlanVersionTestPlanVersionId
        )
    }

    /**
     * Creates a new test threshold.
     *
     * @param request The [TestThresholdRequestDTO] containing threshold details.
     * @return [TestThresholdResponseDTO] representing the created threshold.
     */
    override fun createTestThreshold(request: TestThresholdRequestDTO): TestThresholdResponseDTO {
        val newThreshold = TestThreshold(
            testThresholdId = null,
            targetValue = request.targetValue,
            thresholdTypeThresholdTypeId = request.thresholdTypeThresholdTypeId,
            testPlanVersionTestPlanVersionId = request.testPlanVersionTestPlanVersionId,
            metricOutputMetricOutputId = request.metricOutputMetricOutputId
        )
        val thresholdId = thresholdRepository.save(newThreshold)

        return TestThresholdResponseDTO(
            testThresholdId = thresholdId,
            thresholdTypeThresholdTypeId = request.thresholdTypeThresholdTypeId,
            targetValue = request.targetValue,
            metricOutputMetricOutputId = request.metricOutputMetricOutputId,
            testPlanVersionTestPlanVersionId = request.testPlanVersionTestPlanVersionId
        )
    }
}