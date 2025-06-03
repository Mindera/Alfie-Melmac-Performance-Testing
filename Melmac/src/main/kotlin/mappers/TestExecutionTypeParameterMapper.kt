package mappers

import domain.TestExecutionTypeParameter
import dtos.TestExecutionTypeParameterResponseDTO
import dtos.TestExecutionTypeParameterRequestDTO

object TestExecutionTypeParameterMapper {
    fun toDto(param: TestExecutionTypeParameter): TestExecutionTypeParameterResponseDTO {
        return TestExecutionTypeParameterResponseDTO(
            testExecutionTypeParameterId = param.testExecutionTypeParameterId
                ?: throw IllegalStateException("TestExecutionTypeParameter ID cannot be null"),
            parameterValue = param.parameterValue,
            executionTypeParameterExecutionTypeParameterId = param.executionTypeParameterExecutionTypeParameterId,
            testPlanVersionTestPlanVersionId = param.testPlanVersionTestPlanVersionId
        )
    }

    fun toDomain(dto: TestExecutionTypeParameterResponseDTO): TestExecutionTypeParameter {
        return TestExecutionTypeParameter(
            testExecutionTypeParameterId = dto.testExecutionTypeParameterId,
            parameterValue = dto.parameterValue,
            executionTypeParameterExecutionTypeParameterId = dto.executionTypeParameterExecutionTypeParameterId,
            testPlanVersionTestPlanVersionId = dto.testPlanVersionTestPlanVersionId
        )
    }

    fun fromRequestDto(dto: TestExecutionTypeParameterRequestDTO, testPlanVersionId: Int,
                       executionTypeParameterId: Int): TestExecutionTypeParameter {
        return TestExecutionTypeParameter(
            parameterValue = dto.parameterValue,
            executionTypeParameterExecutionTypeParameterId = executionTypeParameterId,
            testPlanVersionTestPlanVersionId = testPlanVersionId
        )
    }
}