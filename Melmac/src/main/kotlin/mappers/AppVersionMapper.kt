package mappers

import domain.AppVersion
import dtos.AppVersionResponseDTO

object AppVersionMapper {
    fun toDto(appVersion: AppVersion): AppVersionResponseDTO {
        return AppVersionResponseDTO(
            appVersionId = appVersion.appVersionId ?: throw IllegalStateException("AppVersion ID cannot be null"),
            appId = appVersion.appId,
            appVersion = appVersion.appVersion
        )
    }

    fun toDomain(appVersionResponseDTO: AppVersionResponseDTO): AppVersion {
        return AppVersion(
            appVersionId = appVersionResponseDTO.appVersionId,
            appId = appVersionResponseDTO.appId,
            appVersion = appVersionResponseDTO.appVersion
        )
    }
}