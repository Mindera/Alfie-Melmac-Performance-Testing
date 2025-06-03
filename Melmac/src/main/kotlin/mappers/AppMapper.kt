package mappers

import domain.App
import dtos.AppResponseDTO

object AppMapper {
    fun toDto(app: App): AppResponseDTO {
        return AppResponseDTO(
            appId = app.appId ?: throw IllegalStateException("App ID cannot be null"),
            appName = app.appName
        )
    }

    fun toDomain(appResponseDTO: AppResponseDTO): App {
        return App(
            appId = appResponseDTO.appId,
            appName = appResponseDTO.appName
        )
    }
}