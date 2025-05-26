package domain

import java.time.LocalDateTime

data class TestExecution(
    val testExecutionId: Int? = null,
    val initialTimestamp: LocalDateTime,
    val endTimestamp: LocalDateTime,
    val passed: String,
    val testPlanVersionTestPlanVersionId: Int
)
