package services

import domain.*
import dtos.*
import repos.IRepos.IThresholdRepository
import services.IServices.IThresholdService
import java.time.LocalDateTime

class ThresholdService(
    private val thresholdRepository: IThresholdRepository
) : IThresholdService {

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

