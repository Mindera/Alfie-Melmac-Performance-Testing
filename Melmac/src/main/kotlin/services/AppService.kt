package services

import domain.App
import domain.AppVersion
import dtos.AppResponseDTO
import dtos.AppVersionResponseDTO
import java.io.File
import java.nio.file.Paths
import repos.IRepos.IAppRepository
import repos.IRepos.IAppVersionRepository
import services.IServices.IAppService
import utils.Tools
import mappers.AppMapper
import mappers.AppVersionMapper

/**
 * Service implementation for managing applications and their versions.
 * Provides methods to interact with both the database and the file system
 * to retrieve app and version information.
 *
 * @property appRepository Repository for app entities.
 * @property appVersionRepository Repository for app version entities.
 */
class AppService(
        private val appRepository: IAppRepository,
        private val appVersionRepository: IAppVersionRepository
) : IAppService {

    // --- DB Methods ---

    /**
     * Retrieves all apps from the database.
     *
     * @return List of [AppResponseDTO] representing all apps.
     */
    override fun getAllAppsFromDatabase(): List<AppResponseDTO> {
        val apps = appRepository.findAll()
        return apps.map { app -> AppMapper.toDto(app) }
    }

    /**
     * Retrieves all versions for a given app ID from the database.
     *
     * @param appId The ID of the app.
     * @return List of [AppVersionResponseDTO] for the app.
     * @throws IllegalArgumentException if the app is not found.
     */
    override fun getAppVersionsByAppIdFromDatabase(appId: Int): List<AppVersionResponseDTO> {
        val app =
                appRepository.findById(appId)
                        ?: throw IllegalArgumentException("App with id '$appId' not found")

        val versions =
                appVersionRepository.findByAppId(
                        app.appId ?: throw IllegalStateException("App ID cannot be null")
                )

        return versions.map { version: AppVersion -> AppVersionMapper.toDto(version) }
    }

    /**
     * Retrieves an app by its ID from the database.
     *
     * @param appId The ID of the app.
     * @return [AppResponseDTO] for the app.
     * @throws IllegalArgumentException if the app is not found.
     */
    override fun getAppByIdFromDatabase(appId: Int): AppResponseDTO {
        val app =
                appRepository.findById(appId)
                        ?: throw IllegalArgumentException("App with id '$appId' not found")

        return AppMapper.toDto(app)
    }

    /**
     * Retrieves an app version by its ID from the database.
     *
     * @param appVersionId The ID of the app version.
     * @return [AppVersionResponseDTO] for the app version.
     * @throws IllegalArgumentException if the app version is not found.
     */
    override fun getAppVersionByIdFromDatabase(appVersionId: Int): AppVersionResponseDTO {
        val appVersion =
                appVersionRepository.findById(appVersionId)
                        ?: throw IllegalArgumentException("App Version with id '$appVersionId' not found")

        return AppVersionMapper.toDto(appVersion)
    }

    /**
     * Retrieves the app associated with a given app version ID from the database.
     *
     * @param appVersionId The ID of the app version.
     * @return [AppResponseDTO] for the app.
     * @throws IllegalArgumentException if the app or app version is not found.
     */
    override fun getAppByVersionIdFromDatabase(appVersionId: Int): AppResponseDTO {
        val appVersion =
                appVersionRepository.findById(appVersionId)
                        ?: throw IllegalArgumentException("App Version with id '$appVersionId' not found")

        val app =
                appRepository.findById(appVersion.appId)
                        ?: throw IllegalArgumentException("App with id '${appVersion.appId}' not found")

        return AppMapper.toDto(app)
    }

    // --- Folder Methods ---

    /**
     * Retrieves all apps from the apps folder in the file system.
     *
     * @return List of [AppResponseDTO] representing all apps found in the folder.
     * @throws IllegalArgumentException if the folder path is invalid.
     */
    override fun getAllAppsFromFolder(): List<AppResponseDTO> {
        val appFolder = Paths.get(System.getProperty("user.dir"), "src/main/resources/apps")
        val folder = File(appFolder.toString())
        if (!folder.exists() || !folder.isDirectory) {
            throw IllegalArgumentException("Invalid folder path: ${folder.absolutePath}")
        }

        val files =
                folder.listFiles { file ->
                    (file.isFile && file.extension == "apk") ||
                            (Tools.isMac() && file.isDirectory && file.extension == "app")
                }
                        ?: emptyArray()

        val appNames = files.map { it.name }.distinct()

        return appNames.mapIndexed { index, appName ->
            AppMapper.toDto(App(appId = index + 1, appName = appName))
        }
    }

    /**
     * Retrieves an app by its name from the apps folder.
     *
     * @param appName The name of the app.
     * @return [AppResponseDTO] for the app.
     * @throws IllegalArgumentException if the app is not found or folder path is invalid.
     */
    override fun getAppByNameFromFolder(appName: String): AppResponseDTO {
        val appFolder = Paths.get(System.getProperty("user.dir"), "src/main/resources/apps")
        val folder = File(appFolder.toString())
        if (!folder.exists() || !folder.isDirectory) {
            throw IllegalArgumentException("Invalid folder path: ${folder.absolutePath}")
        }

        val files =
                folder.listFiles { file ->
                    (file.isFile && file.extension == "apk" && file.name == appName) ||
                            (Tools.isMac() &&
                                    file.isDirectory &&
                                    file.extension == "app" &&
                                    file.name == appName)
                }
                        ?: emptyArray()

        if (files.isEmpty()) {
            throw IllegalArgumentException(
                    "No app found with name '$appName' in folder '${folder.absolutePath}'"
            )
        }

        return AppMapper.toDto(App(appId = 1, appName = appName))
    }

    /**
     * Retrieves all versions for a given app name from the apps folder.
     *
     * @param appName The name of the app.
     * @return List of [AppVersionResponseDTO] for the app.
     * @throws IllegalArgumentException if no versions are found or folder path is invalid.
     */
    override fun getAppVersionsFromFolder(appName: String): List<AppVersionResponseDTO> {
        val appFolder = Paths.get(System.getProperty("user.dir"), "src/main/resources/apps")
        val folder = File(appFolder.toString())
        if (!folder.exists() || !folder.isDirectory) {
            throw IllegalArgumentException("Invalid folder path: ${folder.absolutePath}")
        }

        val files =
                folder.listFiles { file ->
                    (file.isFile && file.extension == "apk" && file.name == appName) ||
                            (Tools.isMac() &&
                                    file.isDirectory &&
                                    file.extension == "app" &&
                                    file.name == appName)
                }
                        ?: emptyArray()

        if (files.isEmpty()) {
            throw IllegalArgumentException(
                    "No versions found for app '$appName' in folder '${folder.absolutePath}'"
            )
        }

        return files.mapIndexed { index, file ->
            val version =
                    when {
                        file.isFile && file.extension == "apk" ->
                                Tools.getApkVersion(file.absolutePath)
                        file.isDirectory && file.extension == "app" ->
                                Tools.getAppBundleVersion(file.absolutePath)
                        else -> null
                    }
                            ?: "unknown"

            AppVersionMapper.toDto(
                AppVersion(
                    appVersionId = index + 1,
                    appId = index + 1,
                    appVersion = version
                )
            )
        }
    }

    /**
     * Retrieves a specific app version by app name and version from the apps folder.
     *
     * @param appName The name of the app.
     * @param appVersion The version of the app.
     * @return [AppVersionResponseDTO] for the app version.
     * @throws IllegalArgumentException if the app is not found or folder path is invalid.
     */
    override fun getAppVersionByNameFromFolder(
            appName: String,
            appVersion: String
    ): AppVersionResponseDTO {
        val appFolder = Paths.get(System.getProperty("user.dir"), "src/main/resources/apps")
        val folder = File(appFolder.toString())
        if (!folder.exists() || !folder.isDirectory) {
            throw IllegalArgumentException("Invalid folder path: ${folder.absolutePath}")
        }

        val files =
                folder.listFiles { file ->
                    (file.isFile && file.extension == "apk" && file.name == appName) ||
                            (Tools.isMac() &&
                                    file.isDirectory &&
                                    file.extension == "app" &&
                                    file.name == appName)
                }
                        ?: emptyArray()

        if (files.isEmpty()) {
            throw IllegalArgumentException(
                    "No app found with name '$appName' in folder '${folder.absolutePath}'"
            )
        }

        return AppVersionMapper.toDto(
            AppVersion(
                appVersionId = 1,
                appId = 1,
                appVersion = appVersion
            )
        )
    }
}