package domain.dtos

data class TestSuiteRequestDTO(
    val name: String,
    val description: String? = null
)
