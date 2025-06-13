import mappers.ThresholdTypeMapper
import domain.ThresholdType
import dtos.ThresholdTypeResponseDTO
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ThresholdTypeMapperTest {
    @Test
    fun `toDto maps ThresholdType to ThresholdTypeResponseDTO correctly`() {
        val type = ThresholdType(1, "Upper", "Upper bound")
        val dto = ThresholdTypeMapper.toDto(type)
        assertEquals(1, dto.thresholdTypeId)
        assertEquals("Upper", dto.thresholdTypeName)
        assertEquals("Upper bound", dto.thresholdTypeDescription)
    }

    @Test
    fun `toDto throws if thresholdTypeId is null`() {
        val type = ThresholdType(null, "Upper", "Upper bound")
        assertThrows(IllegalStateException::class.java) {
            ThresholdTypeMapper.toDto(type)
        }
    }

    @Test
    fun `toDomain maps ThresholdTypeResponseDTO to ThresholdType correctly`() {
        val dto = ThresholdTypeResponseDTO(2, "Lower", "Lower bound")
        val type = ThresholdTypeMapper.toDomain(dto)
        assertEquals(2, type.thresholdTypeId)
        assertEquals("Lower", type.thresholdTypeName)
        assertEquals("Lower bound", type.thresholdTypeDescription)
    }
}