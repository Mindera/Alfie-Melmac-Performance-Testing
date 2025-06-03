package mappers

import domain.TestMetricParameter
import dtos.TestMetricParameterResponseDTO
import dtos.TestMetricParameterRequestDTO

object TestMetricParameterMapper {
    fun toDto(param: TestMetricParameter): TestMetricParameterResponseDTO {
        return TestMetricParameterResponseDTO(
            testMetricParameterId = param.testMetricParameterId
                ?: throw IllegalStateException("TestMetricParameter ID cannot be null"),
            parameterValue = param.parameterValue,
            metricParameterMetricParameterId = param.metricParameterMetricParameterId,
            testPlanVersionTestPlanVersionId = param.testPlanVersionTestPlanVersionId
        )
    }

    fun toDomain(dto: TestMetricParameterResponseDTO): TestMetricParameter {
        return TestMetricParameter(
            testMetricParameterId = dto.testMetricParameterId,
            parameterValue = dto.parameterValue,
            metricParameterMetricParameterId = dto.metricParameterMetricParameterId,
            testPlanVersionTestPlanVersionId = dto.testPlanVersionTestPlanVersionId
        )
    }

    fun fromRequestDto(dto: TestMetricParameterRequestDTO, testPlanVersionId: Int,
                       metricParameterId: Int): TestMetricParameter {
        return TestMetricParameter(
            parameterValue = dto.parameterValue,
            metricParameterMetricParameterId = metricParameterId,
            testPlanVersionTestPlanVersionId = testPlanVersionId
        )
    }
}