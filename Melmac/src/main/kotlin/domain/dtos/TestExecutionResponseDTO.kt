package dtos

import java.time.LocalDateTime

data class TestExecutionResponseDTO(
    val testExecutionId: Int,
    val initialTimestamp: LocalDateTime,
    val endTimestamp: LocalDateTime,
    val passed: String,
    val testPlanVersionTestPlanVersionId: Int
)
