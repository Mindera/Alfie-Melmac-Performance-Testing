import mappers.ExecutionTypeMapper
import domain.ExecutionType
import dtos.ExecutionTypeResponseDTO
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ExecutionTypeMapperTest {
    @Test
    fun `toDto maps ExecutionType to ExecutionTypeResponseDTO correctly`() {
        val executionType = ExecutionType(1, "TypeA", "DescriptionA")
        val dto = ExecutionTypeMapper.toDto(executionType)
        assertEquals(1, dto.executionTypeId)
        assertEquals("TypeA", dto.executionTypeName)
        assertEquals("DescriptionA", dto.executionTypeDescription)
    }

    @Test
    fun `toDto throws if executionTypeId is null`() {
        val executionType = ExecutionType(null, "TypeA", "DescriptionA")
        assertThrows(IllegalStateException::class.java) {
            ExecutionTypeMapper.toDto(executionType)
        }
    }

    @Test
    fun `toDomain maps ExecutionTypeResponseDTO to ExecutionType correctly`() {
        val dto = ExecutionTypeResponseDTO(2, "TypeB", "DescriptionB")
        val executionType = ExecutionTypeMapper.toDomain(dto)
        assertEquals(2, executionType.executionTypeId)
        assertEquals("TypeB", executionType.executionTypeName)
        assertEquals("DescriptionB", executionType.executionTypeDescription)
    }
}