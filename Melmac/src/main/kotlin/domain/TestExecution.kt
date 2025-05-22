package domain

import java.time.Instant

data class TestExecution(
    val id: Int = 0,
    val testSuiteId: Int,
    val appVersionId: Int,
    val deviceId: String,
    val metricId: Int,
    val executionTypeId: Int,
    val startTimestamp: Instant = Instant.now(),
    val endTimestamp: Instant? = null
)