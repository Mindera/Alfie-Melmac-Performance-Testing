package domain

import java.time.Instant

data class AppVersion(
    val id: Int? = null,
    val appId: Int,
    val filePath: String,
    val fileName: String,
    val platform: String,
    val versionName: String,
    val minSdk: Int? = null,
    val minIosVersion: String? = null,
    val uploadedAt: Instant = Instant.now()
)
