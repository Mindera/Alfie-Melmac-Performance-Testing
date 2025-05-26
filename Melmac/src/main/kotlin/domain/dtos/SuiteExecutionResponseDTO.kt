package dtos

import java.time.LocalDateTime

data class SuiteExecutionResponseDTO(
    val suiteExecutionId: Int,
    val initialTimestamp: LocalDateTime,
    val endTimestamp: LocalDateTime,
    val testSuiteVersionTestSuiteVersionId: Int,
    val executionResults: List<TestExecutionResponseDTO>
)
