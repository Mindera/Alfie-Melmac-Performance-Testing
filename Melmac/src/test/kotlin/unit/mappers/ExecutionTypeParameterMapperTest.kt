import mappers.ExecutionTypeParameterMapper
import domain.ExecutionTypeParameter
import dtos.ExecutionTypeParameterResponseDTO
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ExecutionTypeParameterMapperTest {
    @Test
    fun `toDto maps ExecutionTypeParameter to ExecutionTypeParameterResponseDTO correctly`() {
        val param = ExecutionTypeParameter(1, "timeout", "int", 2)
        val dto = ExecutionTypeParameterMapper.toDto(param)
        assertEquals(1, dto.executionTypeParameterId)
        assertEquals("timeout", dto.parameterName)
        assertEquals("int", dto.parameterType)
        assertEquals(2, dto.executionTypeExecutionTypeId)
    }

    @Test
    fun `toDto throws if executionTypeParameterId is null`() {
        val param = ExecutionTypeParameter(null, "timeout", "int", 2)
        assertThrows(IllegalStateException::class.java) {
            ExecutionTypeParameterMapper.toDto(param)
        }
    }

    @Test
    fun `toDomain maps ExecutionTypeParameterResponseDTO to ExecutionTypeParameter correctly`() {
        val dto = ExecutionTypeParameterResponseDTO(3, "interval", "long", 4)
        val param = ExecutionTypeParameterMapper.toDomain(dto)
        assertEquals(3, param.executionTypeParameterId)
        assertEquals("interval", param.parameterName)
        assertEquals("long", param.parameterType)
        assertEquals(4, param.executionTypeExecutionTypeId)
    }
}