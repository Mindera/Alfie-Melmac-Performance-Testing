import mappers.OSVersionMapper
import domain.OSVersion
import dtos.OSVersionResponseDTO
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class OSVersionMapperTest {
    @Test
    fun `toDto maps OSVersion to OSVersionResponseDTO correctly`() {
        val osVersion = OSVersion(1, "12", 2)
        val dto = OSVersionMapper.toDto(osVersion)
        assertEquals(1, dto.osVersionId)
        assertEquals("12", dto.version)
        assertEquals(2, dto.operativeSystemOperSysId)
    }

    @Test
    fun `toDto throws if osVersionId is null`() {
        val osVersion = OSVersion(null, "13", 3)
        assertThrows(IllegalStateException::class.java) {
            OSVersionMapper.toDto(osVersion)
        }
    }

    @Test
    fun `toDomain maps OSVersionResponseDTO to OSVersion correctly`() {
        val dto = OSVersionResponseDTO(4, "14", 5)
        val osVersion = OSVersionMapper.toDomain(dto)
        assertEquals(4, osVersion.osVersionId)
        assertEquals("14", osVersion.version)
        assertEquals(5, osVersion.operativeSystemOperSysId)
    }
}