package mappers

import domain.OSVersion
import dtos.OSVersionResponseDTO

object OSVersionMapper {
    fun toDto(osVersion: OSVersion): OSVersionResponseDTO {
        return OSVersionResponseDTO(
            osVersionId = osVersion.osVersionId ?: throw IllegalStateException("OSVersion ID cannot be null"),
            version = osVersion.version,
            operativeSystemOperSysId = osVersion.operativeSystemOperSysId
        )
    }

    fun toDomain(dto: OSVersionResponseDTO): OSVersion {
        return OSVersion(
            osVersionId = dto.osVersionId,
            version = dto.version,
            operativeSystemOperSysId = dto.operativeSystemOperSysId
        )
    }
}