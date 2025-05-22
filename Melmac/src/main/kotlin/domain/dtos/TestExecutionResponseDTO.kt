package domain.dtos

import java.time.Instant

data class TestExecutionResponseDTO(
    val id: Int,
    val testSuiteId: Int,
    val appVersionId: Int,
    val deviceId: String,
    val metricId: Int,
    val executionTypeId: Int,
    val startTimestamp: Instant,
    val endTimestamp: Instant?,
    val executionParameters: List<ExecutionParameterValueResponseDTO>,
    val metricParameters: List<MetricParameterValueResponseDTO>
)

data class ExecutionParameterValueResponseDTO(
    val id: Int,
    val parameterId: Int,
    val name: String,
    val value: String,
    val type: String
)

data class MetricParameterValueResponseDTO(
    val id: Int,
    val parameterId: Int,
    val name: String,
    val value: String,
    val type: String
)
