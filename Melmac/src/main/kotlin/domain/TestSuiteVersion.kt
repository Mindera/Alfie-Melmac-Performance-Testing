package domain

import java.time.LocalDateTime

data class TestSuiteVersion(
    val testSuiteVersionId: Int? = null,
    val version: String,
    val creationTimestamp: LocalDateTime,
    val notes: String?,
    val testSuiteTestSuiteId: Int
)
