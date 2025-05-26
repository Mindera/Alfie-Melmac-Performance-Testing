package dtos

data class TestSuiteVersionRequestDTO(
    val version: String,
    val notes: String?,
    val testSuiteTestSuiteId: Int
)
