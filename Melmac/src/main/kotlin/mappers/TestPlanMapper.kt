package mappers

import domain.TestPlan
import dtos.TestPlanResponseDTO

object TestPlanMapper {
    fun toDto(testPlan: TestPlan): TestPlanResponseDTO {
        return TestPlanResponseDTO(
            testPlanId = testPlan.testPlanId ?: throw IllegalStateException("TestPlan ID cannot be null"),
            testName = testPlan.testName,
            metricMetricId = testPlan.metricMetricId
        )
    }

    fun toDomain(dto: TestPlanResponseDTO): TestPlan {
        return TestPlan(
            testPlanId = dto.testPlanId,
            testName = dto.testName,
            metricMetricId = dto.metricMetricId
        )
    }
}