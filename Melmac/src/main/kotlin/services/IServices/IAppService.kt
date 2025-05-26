package services.IServices

import dtos.AppResponseDTO
import dtos.AppVersionResponseDTO

interface IAppService {

    fun getAllAppsFromDatabase(): List<AppResponseDTO>
    fun getAppVersionsByAppIdFromDatabase(appId: Int): List<AppVersionResponseDTO>
    fun getAllAppsFromFolder(): List<AppResponseDTO>
    fun getAppVersionsFromFolder(appName: String): List<AppVersionResponseDTO>
    fun getAppByNameFromFolder(appName: String): AppResponseDTO
    fun getAppVersionByNameFromFolder(appName: String, appVersion: String): AppVersionResponseDTO
}
