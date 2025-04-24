package domain.dtos

data class TestMetricResponseDTO(
    val id: Int,
    val testExecutionId: Int,
    val outputId: Int,
    val value: Double
)