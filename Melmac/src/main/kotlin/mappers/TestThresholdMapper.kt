package mappers

import domain.TestThreshold
import dtos.TestThresholdResponseDTO
import dtos.TestThresholdRequestDTO

object TestThresholdMapper {
    fun toDto(threshold: TestThreshold): TestThresholdResponseDTO {
        return TestThresholdResponseDTO(
            testThresholdId = threshold.testThresholdId
                ?: throw IllegalStateException("TestThreshold ID cannot be null"),
            targetValue = threshold.targetValue,
            thresholdTypeThresholdTypeId = threshold.thresholdTypeThresholdTypeId,
            testPlanVersionTestPlanVersionId = threshold.testPlanVersionTestPlanVersionId,
            metricOutputMetricOutputId = threshold.metricOutputMetricOutputId
        )
    }

    fun toDomain(dto: TestThresholdResponseDTO): TestThreshold {
        return TestThreshold(
            testThresholdId = dto.testThresholdId,
            targetValue = dto.targetValue,
            thresholdTypeThresholdTypeId = dto.thresholdTypeThresholdTypeId,
            testPlanVersionTestPlanVersionId = dto.testPlanVersionTestPlanVersionId,
            metricOutputMetricOutputId = dto.metricOutputMetricOutputId
        )
    }
 
    fun fromRequestDto(dto: TestThresholdRequestDTO, testPlanVersionId: Int, thresholdTypeId: Int): TestThreshold {
        return TestThreshold(
            targetValue = dto.targetValue,
            thresholdTypeThresholdTypeId = thresholdTypeId,
            testPlanVersionTestPlanVersionId = testPlanVersionId,
            metricOutputMetricOutputId = dto.metricOutputMetricOutputId
        )
    }
}