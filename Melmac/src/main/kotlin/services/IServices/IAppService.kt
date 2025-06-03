package services.IServices

import dtos.AppResponseDTO
import dtos.AppVersionResponseDTO

/**
 * Service interface for managing applications and their versions.
 * Provides methods to retrieve application data from both the database and the file system.
 * This interface abstracts the data access layer, allowing for flexible implementations.
 */
interface IAppService {

    fun getAllAppsFromDatabase(): List<AppResponseDTO>
    fun getAppVersionsByAppIdFromDatabase(appId: Int): List<AppVersionResponseDTO>
    fun getAllAppsFromFolder(): List<AppResponseDTO>
    fun getAppVersionsFromFolder(appName: String): List<AppVersionResponseDTO>
    fun getAppByNameFromFolder(appName: String): AppResponseDTO
    fun getAppVersionByNameFromFolder(appName: String, appVersion: String): AppVersionResponseDTO
    fun getAppByIdFromDatabase(appId: Int): AppResponseDTO
    fun getAppVersionByIdFromDatabase(appVersionId: Int): AppVersionResponseDTO
    fun getAppByVersionIdFromDatabase(appVersionId: Int): AppResponseDTO
}