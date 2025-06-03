package services

import domain.*
import dtos.*
import repos.IRepos.IThresholdRepository
import repos.IRepos.IThresholdTypeRepository
import services.IServices.IThresholdService
import mappers.TestThresholdMapper
import java.time.LocalDateTime

/**
 * Service implementation for managing Test Thresholds.
 *
 * Provides methods to retrieve and create test thresholds associated with test plan versions.
 *
 * @property thresholdRepository Repository for TestThreshold entities.
 */
class ThresholdService(
    private val thresholdRepository: IThresholdRepository,
    private val thresholdTypeRepository: IThresholdTypeRepository
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
            TestThresholdMapper.toDto(threshold)
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
        return TestThresholdMapper.toDto(threshold)
    }

    /**
     * Creates a new test threshold.
     *
     * @param request The [TestThresholdRequestDTO] containing threshold details.
     * @return [TestThresholdResponseDTO] representing the created threshold.
     */
    override fun createTestThreshold(request: TestThresholdRequestDTO): TestThresholdResponseDTO {
        val thresholdTypeId = thresholdTypeRepository
            .findByName(request.thresholdType)
            ?.thresholdTypeId
            ?: throw IllegalArgumentException("Threshold type not found: ${request.thresholdType}")
        val newThreshold = TestThresholdMapper.fromRequestDto(
            request,
            request.testPlanVersionTestPlanVersionId,
            thresholdTypeId
        )
        val thresholdId = thresholdRepository.save(newThreshold)

        return TestThresholdMapper.toDto(
            newThreshold.copy(testThresholdId = thresholdId)
        )
    }
}