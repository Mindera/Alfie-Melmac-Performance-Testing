package dtos

import java.time.LocalDateTime

data class TestSuiteVersionResponseDTO(
    val testSuiteVersionId: Int,
    val version: String,
    val creationTimestamp: LocalDateTime,
    val notes: String?,
    val testSuiteTestSuiteId: Int
)
