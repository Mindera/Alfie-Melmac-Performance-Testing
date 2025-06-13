import mappers.TestSuiteMapper
import domain.TestSuite
import dtos.TestSuiteResponseDTO
import dtos.TestSuiteRequestDTO
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class TestSuiteMapperTest {
    @Test
    fun `toDto maps TestSuite to TestSuiteResponseDTO correctly`() {
        val suite = TestSuite(1, "SuiteA", "desc", LocalDateTime.MIN, true)
        val dto = TestSuiteMapper.toDto(suite)
        assertEquals(1, dto.testSuiteId)
        assertEquals("SuiteA", dto.testSuiteName)
        assertEquals("desc", dto.testSuiteDescription)
        assertEquals(LocalDateTime.MIN, dto.creationTimestamp)
        assertTrue(dto.isActive)
    }

    @Test
    fun `toDto throws if testSuiteId is null`() {
        val suite = TestSuite(null, "SuiteA", "desc", LocalDateTime.MIN, true)
        assertThrows(IllegalStateException::class.java) {
            TestSuiteMapper.toDto(suite)
        }
    }

    @Test
    fun `toDomain maps TestSuiteResponseDTO to TestSuite correctly`() {
        val dto = TestSuiteResponseDTO(2, "SuiteB", "desc2", LocalDateTime.MAX, false)
        val suite = TestSuiteMapper.toDomain(dto)
        assertEquals(2, suite.testSuiteId)
        assertEquals("SuiteB", suite.testSuiteName)
        assertEquals("desc2", suite.testSuiteDescription)
        assertEquals(LocalDateTime.MAX, suite.creationTimestamp)
        assertFalse(suite.isActive)
    }

    @Test
    fun `fromRequestDto maps TestSuiteRequestDTO and params to TestSuite correctly`() {
        val req = TestSuiteRequestDTO("SuiteC", "desc3")
        val now = LocalDateTime.now()
        val suite = TestSuiteMapper.fromRequestDto(req, now, true)
        assertNull(suite.testSuiteId)
        assertEquals("SuiteC", suite.testSuiteName)
        assertEquals("desc3", suite.testSuiteDescription)
        assertEquals(now, suite.creationTimestamp)
        assertTrue(suite.isActive)
    }
}