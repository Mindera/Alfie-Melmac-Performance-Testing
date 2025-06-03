package domain

import java.time.LocalDateTime

data class TestSuite(
    val testSuiteId: Int? = null,
    val testSuiteName: String,
    val testSuiteDescription: String?,
    val creationTimestamp: LocalDateTime,
    val isActive: Boolean
)
