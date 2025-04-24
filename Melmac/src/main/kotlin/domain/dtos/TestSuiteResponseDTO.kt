package domain.dtos

data class TestSuiteResponseDTO(
    val id: Int,
    val name: String,
    val description: String? = null,
    val initialTimestamp: String? = null,
    val endTimestamp: String? = null,
)