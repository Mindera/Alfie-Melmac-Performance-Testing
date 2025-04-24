package domain.dtos

import java.time.Instant

data class TestExecutionRequestDTO(
    val testSuiteId: Int,
    val appVersionId: Int,
    val deviceId: String,
    val metricId: Int,
    val executionTypeId: Int,
    val executionParameters: List<ExecutionParameterValueDTO>,
    val metricParameters: List<MetricParameterValueDTO>,
    val startTimestamp: Instant? = null,
    val endTimestamp: Instant? = null
)

data class ExecutionParameterValueDTO(
    val parameterId: Int,
    val value: String
)

data class MetricParameterValueDTO(
    val parameterId: Int,
    val value: String
)