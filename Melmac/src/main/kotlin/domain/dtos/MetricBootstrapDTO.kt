package domain.dtos

data class MetricConfigFileDTO(
    val metrics: List<MetricBootstrapDTO>
)

data class MetricBootstrapDTO(
    val name: String,
    val description: String?,
    val outputs: List<OutputDTO>,
    val executionTypes: List<ExecutionTypeDTO>,
    val metricParameters: List<ParameterBootstrapDTO>
)

data class ExecutionTypeDTO(
    val name: String,
    val description: String?,
    val parameters: List<ParameterBootstrapDTO>
)

data class OutputDTO(
    val name: String,
    val unit: String,
)

data class ParameterBootstrapDTO(
    val name: String,
    val type: String
)