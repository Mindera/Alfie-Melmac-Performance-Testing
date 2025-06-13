import mappers.OperSysMapper
import domain.OperativeSystem
import dtos.OperativeSystemResponseDTO
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class OperSysMapperTest {
    @Test
    fun `toDto maps OperativeSystem to OperativeSystemResponseDTO correctly`() {
        val os = OperativeSystem(1, "Android")
        val dto = OperSysMapper.toDto(os)
        assertEquals(1, dto.operSysId)
        assertEquals("Android", dto.operSysName)
    }

    @Test
    fun `toDto throws if operSysId is null`() {
        val os = OperativeSystem(null, "iOS")
        assertThrows(IllegalStateException::class.java) {
            OperSysMapper.toDto(os)
        }
    }

    @Test
    fun `toDomain maps OperativeSystemResponseDTO to OperativeSystem correctly`() {
        val dto = OperativeSystemResponseDTO(2, "Linux")
        val os = OperSysMapper.toDomain(dto)
        assertEquals(2, os.operSysId)
        assertEquals("Linux", os.operSysName)
    }
}