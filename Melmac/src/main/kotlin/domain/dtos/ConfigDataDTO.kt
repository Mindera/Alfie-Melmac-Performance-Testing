package dtos

data class DataConfig(
    val metrics: List<MetricConfigDTO>,
    val thresholdTypes: List<ThresholdTypeConfigDTO>
)

data class MetricConfigDTO(
    val name: String,
    val outputs: List<MetricOutputConfigDTO> = emptyList(),
    val executionTypes: List<ExecutionTypeConfigDTO> = emptyList(),
    val metricParameters: List<MetricParameterConfigDTO> = emptyList()
)

data class MetricOutputConfigDTO(
    val outputName: String?,
    val unit: String
)

data class ExecutionTypeConfigDTO(
    val executionTypeName: String,
    val executionTypeDescription: String,
    val parameters: List<ExecutionTypeParameterConfigDTO> = emptyList()
)

data class ExecutionTypeParameterConfigDTO(
    val executionTypeParameterName: String,
    val executionTypeParameterType: String
)

data class MetricParameterConfigDTO(
    val parameterName: String,
    val parameterType: String
)

data class ThresholdTypeConfigDTO(
    val thresholdTypeName: String,
    val thresholdTypeDescription: String? = null,
)
