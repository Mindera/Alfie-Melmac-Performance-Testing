package domain.dtos

import java.time.Instant

data class AppVersionResponseDTO(
    val id: Int,
    val appId: Int,
    val fileName: String,
    val platform: String,
    val versionName: String,
    val uploadedAt: Instant
)
