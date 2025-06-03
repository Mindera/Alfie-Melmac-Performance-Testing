package mappers

import domain.ExecutionTypeParameter
import dtos.ExecutionTypeParameterResponseDTO

object ExecutionTypeParameterMapper {
    fun toDto(executionTypeParameter: ExecutionTypeParameter): ExecutionTypeParameterResponseDTO {
        return ExecutionTypeParameterResponseDTO(
            executionTypeParameterId = executionTypeParameter.executionTypeParameterId
                ?: throw IllegalStateException("ExecutionTypeParameter ID cannot be null"),
            parameterName = executionTypeParameter.parameterName,
            parameterType = executionTypeParameter.parameterType,
            executionTypeExecutionTypeId = executionTypeParameter.executionTypeExecutionTypeId
        )
    }

    fun toDomain(dto: ExecutionTypeParameterResponseDTO): ExecutionTypeParameter {
        return ExecutionTypeParameter(
            executionTypeParameterId = dto.executionTypeParameterId,
            parameterName = dto.parameterName,
            parameterType = dto.parameterType,
            executionTypeExecutionTypeId = dto.executionTypeExecutionTypeId
        )
    }
}