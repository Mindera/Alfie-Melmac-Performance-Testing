package domain.dtos

data class AppRegistrationRequestDTO(
    val appName: String,
    val version: AppVersionRequestDTO
)

data class AppVersionRequestDTO(
    val filePath: String,
    val fileName: String,
    val platform: String,
    val versionName: String,
    val minSdk: Int? = null,
    val minIosVersion: String? = null
)
