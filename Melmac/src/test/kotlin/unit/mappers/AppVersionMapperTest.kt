import mappers.AppVersionMapper
import domain.AppVersion
import dtos.AppVersionResponseDTO
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AppVersionMapperTest {
    @Test
    fun `toDto maps AppVersion to AppVersionResponseDTO correctly`() {
        val appVersion = AppVersion(1, 2, "1.0.0")
        val dto = AppVersionMapper.toDto(appVersion)
        assertEquals(1, dto.appVersionId)
        assertEquals(2, dto.appId)
        assertEquals("1.0.0", dto.appVersion)
    }

    @Test
    fun `toDto throws if appVersionId is null`() {
        val appVersion = AppVersion(null, 2, "1.0.0")
        assertThrows(IllegalStateException::class.java) {
            AppVersionMapper.toDto(appVersion)
        }
    }

    @Test
    fun `toDomain maps AppVersionResponseDTO to AppVersion correctly`() {
        val dto = AppVersionResponseDTO(3, 4, "2.0.0")
        val appVersion = AppVersionMapper.toDomain(dto)
        assertEquals(3, appVersion.appVersionId)
        assertEquals(4, appVersion.appId)
        assertEquals("2.0.0", appVersion.appVersion)
    }
}