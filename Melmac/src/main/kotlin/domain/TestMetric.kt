package domain

data class TestMetric(
    val id: Int? = null,
    val testExecutionId: Int,
    val outputId: Int,
    val value: Double
)