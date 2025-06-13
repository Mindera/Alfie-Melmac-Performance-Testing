import mappers.SuiteExecutionMapper
import domain.SuiteExecution
import dtos.SuiteExecutionResponseDTO
import dtos.TestExecutionResponseDTO
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class SuiteExecutionMapperTest {
    @Test
    fun `toDto maps SuiteExecution and executionResults to SuiteExecutionResponseDTO correctly`() {
        val suiteExecution = SuiteExecution(1, LocalDateTime.MIN, LocalDateTime.MAX, 42)
        val execResults = listOf(
            TestExecutionResponseDTO(10, LocalDateTime.MIN, LocalDateTime.MAX, "true", 99)
        )
        val dto = SuiteExecutionMapper.toDto(suiteExecution, execResults)
        assertEquals(1, dto.suiteExecutionId)
        assertEquals(LocalDateTime.MIN, dto.initialTimestamp)
        assertEquals(LocalDateTime.MAX, dto.endTimestamp)
        assertEquals(42, dto.testSuiteVersionTestSuiteVersionId)
        assertEquals(execResults, dto.executionResults)
    }

    @Test
    fun `toDto throws if suiteExecutionId is null`() {
        val suiteExecution = SuiteExecution(null, LocalDateTime.MIN, LocalDateTime.MAX, 42)
        val execResults = emptyList<TestExecutionResponseDTO>()
        assertThrows(IllegalStateException::class.java) {
            SuiteExecutionMapper.toDto(suiteExecution, execResults)
        }
    }

    @Test
    fun `toDomain maps SuiteExecutionResponseDTO to SuiteExecution correctly`() {
        val dto = SuiteExecutionResponseDTO(2, LocalDateTime.MIN, LocalDateTime.MAX, 77, emptyList())
        val suiteExecution = SuiteExecutionMapper.toDomain(dto)
        assertEquals(2, suiteExecution.suiteExecutionId)
        assertEquals(LocalDateTime.MIN, suiteExecution.initialTimestamp)
        assertEquals(LocalDateTime.MAX, suiteExecution.endTimestamp)
        assertEquals(77, suiteExecution.testSuiteVersionTestSuiteVersionId)
    }
}