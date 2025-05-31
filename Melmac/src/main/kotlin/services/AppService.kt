package services

import domain.AppVersion
import dtos.AppResponseDTO
import dtos.AppVersionResponseDTO
import java.io.File
import java.nio.file.Paths
import repos.IRepos.IAppRepository
import repos.IRepos.IAppVersionRepository
import services.IServices.IAppService
import utils.Tools

class AppService(
        private val appRepository: IAppRepository,
        private val appVersionRepository: IAppVersionRepository
) : IAppService {

    // --- DB Methods ---

    override fun getAllAppsFromDatabase(): List<AppResponseDTO> {
        val apps = appRepository.findAll()
        return apps.map { app ->
            AppResponseDTO(
                    appId = app.appId ?: throw IllegalStateException("App ID cannot be null"),
                    appName = app.appName
            )
        }
    }

    override fun getAppVersionsByAppIdFromDatabase(appId: Int): List<AppVersionResponseDTO> {
        val app =
                appRepository.findById(appId)
                        ?: throw IllegalArgumentException("App with id '$appId' not found")

        val versions =
                appVersionRepository.findByAppId(
                        app.appId ?: throw IllegalStateException("App ID cannot be null")
                )

        return versions.map { version: AppVersion ->
            AppVersionResponseDTO(
                    appVersionId = version.appVersionId
                                    ?: throw IllegalStateException("AppVersion ID cannot be null"),
                    appId = version.appId,
                    appVersion = version.appVersion
            )
        }
    }

    override fun getAppByIdFromDatabase(appId: Int): AppResponseDTO {
        val app =
                appRepository.findById(appId)
                        ?: throw IllegalArgumentException("App with id '$appId' not found")

        return AppResponseDTO(
                appId = app.appId ?: throw IllegalStateException("App ID cannot be null"),
                appName = app.appName
        )
    }

    override fun getAppVersionByIdFromDatabase(appVersionId: Int): AppVersionResponseDTO {
        val appVersion =
                appVersionRepository.findById(appVersionId)
                        ?: throw IllegalArgumentException("App Version with id '$appVersionId' not found")

        return AppVersionResponseDTO(
                appVersionId = appVersion.appVersionId
                                ?: throw IllegalStateException("AppVersion ID cannot be null"),
                appId = appVersion.appId,
                appVersion = appVersion.appVersion
        )
    }

    // --- Folder Methods ---
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
            AppResponseDTO(appId = index + 1, appName = appName)
        }
    }

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

        return AppResponseDTO(appId = 1, appName = appName)
    }

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

            AppVersionResponseDTO(appVersionId = index + 1, appId = index + 1, appVersion = version)
        }
    }

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

        return AppVersionResponseDTO(appVersionId = 1, appId = 1, appVersion = appVersion)
    }
}
