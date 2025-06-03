package dtos

import java.time.LocalDateTime

data class TestSuiteResponseDTO(
    val testSuiteId: Int,
    val testSuiteName: String,
    val testSuiteDescription: String?,
    val creationTimestamp: LocalDateTime,
    val isActive: Boolean
)
