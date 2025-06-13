import mappers.TestExecutionTypeParameterMapper
import domain.TestExecutionTypeParameter
import dtos.TestExecutionTypeParameterResponseDTO
import dtos.TestExecutionTypeParameterRequestDTO
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TestExecutionTypeParameterMapperTest {
    @Test
    fun `toDto maps TestExecutionTypeParameter to TestExecutionTypeParameterResponseDTO correctly`() {
        val param = TestExecutionTypeParameter(1, "val", 2, 3)
        val dto = TestExecutionTypeParameterMapper.toDto(param)
        assertEquals(1, dto.testExecutionTypeParameterId)
        assertEquals("val", dto.parameterValue)
        assertEquals(2, dto.executionTypeParameterExecutionTypeParameterId)
        assertEquals(3, dto.testPlanVersionTestPlanVersionId)
    }

    @Test
    fun `toDto throws if testExecutionTypeParameterId is null`() {
        val param = TestExecutionTypeParameter(null, "val", 2, 3)
        assertThrows(IllegalStateException::class.java) {
            TestExecutionTypeParameterMapper.toDto(param)
        }
    }

    @Test
    fun `toDomain maps TestExecutionTypeParameterResponseDTO to TestExecutionTypeParameter correctly`() {
        val dto = TestExecutionTypeParameterResponseDTO(4, "foo", 5, 6)
        val param = TestExecutionTypeParameterMapper.toDomain(dto)
        assertEquals(4, param.testExecutionTypeParameterId)
        assertEquals("foo", param.parameterValue)
        assertEquals(5, param.executionTypeParameterExecutionTypeParameterId)
        assertEquals(6, param.testPlanVersionTestPlanVersionId)
    }

    @Test
    fun `fromRequestDto maps request DTO and ids to TestExecutionTypeParameter correctly`() {
        val req = TestExecutionTypeParameterRequestDTO("bar", "8", 7)
        val param = TestExecutionTypeParameterMapper.fromRequestDto(req, testPlanVersionId = 7, executionTypeParameterId = 8)
        assertNull(param.testExecutionTypeParameterId)
        assertEquals("bar", param.parameterValue)
        assertEquals(8, param.executionTypeParameterExecutionTypeParameterId)
        assertEquals(7, param.testPlanVersionTestPlanVersionId)
    }
}