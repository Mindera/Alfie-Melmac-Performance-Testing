package domain

import java.time.LocalDateTime

data class SuiteExecution(
    val suiteExecutionId: Int? = null,
    val initialTimestamp: LocalDateTime,
    val endTimestamp: LocalDateTime,
    val testSuiteVersionTestSuiteVersionId: Int
)
