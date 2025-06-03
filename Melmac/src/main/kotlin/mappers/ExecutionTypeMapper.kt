package mappers

import domain.ExecutionType
import dtos.ExecutionTypeResponseDTO

object ExecutionTypeMapper {
    fun toDto(executionType: ExecutionType): ExecutionTypeResponseDTO {
        return ExecutionTypeResponseDTO(
            executionTypeId = executionType.executionTypeId ?: throw IllegalStateException("ExecutionType ID cannot be null"),
            executionTypeName = executionType.executionTypeName,
            executionTypeDescription = executionType.executionTypeDescription
        )
    }

    fun toDomain(dto: ExecutionTypeResponseDTO): ExecutionType {
        return ExecutionType(
            executionTypeId = dto.executionTypeId,
            executionTypeName = dto.executionTypeName,
            executionTypeDescription = dto.executionTypeDescription
        )
    }
}