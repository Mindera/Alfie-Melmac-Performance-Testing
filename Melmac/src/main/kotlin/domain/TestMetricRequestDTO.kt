package domain.dtos

data class TestMetricRequestDTO(
    val testExecutionId: Int,
    val outputId: Int,
    val value: Double
)
