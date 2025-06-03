package mappers

import domain.OperativeSystem
import dtos.OperativeSystemResponseDTO

object OperSysMapper {
    fun toDto(operativeSystem: OperativeSystem): OperativeSystemResponseDTO {
        return OperativeSystemResponseDTO(
            operSysId = operativeSystem.operSysId ?: throw IllegalStateException("OperSys ID cannot be null"),
            operSysName = operativeSystem.operSysName
        )
    }

    fun toDomain(dto: OperativeSystemResponseDTO): OperativeSystem {
        return OperativeSystem(
            operSysId = dto.operSysId,
            operSysName = dto.operSysName
        )
    }
}