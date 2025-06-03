package mappers

import domain.TestMetricOutputResult
import dtos.TestMetricOutputResultResponseDTO

object TestMetricOutputResultMapper {
    fun toDto(result: TestMetricOutputResult): TestMetricOutputResultResponseDTO {
        return TestMetricOutputResultResponseDTO(
            testMetricOutputResultId = result.testMetricOutputResultId
                ?: throw IllegalStateException("TestMetricOutputResult ID cannot be null"),
            value = result.value,
            metricOutputMetricOutputId = result.metricOutputMetricOutputId,
            testExecutionTestExecutionId = result.testExecutionTestExecutionId
        )
    }

    fun toDomain(dto: TestMetricOutputResultResponseDTO): TestMetricOutputResult {
        return TestMetricOutputResult(
            testMetricOutputResultId = dto.testMetricOutputResultId,
            value = dto.value,
            metricOutputMetricOutputId = dto.metricOutputMetricOutputId,
            testExecutionTestExecutionId = dto.testExecutionTestExecutionId
        )
    }
}