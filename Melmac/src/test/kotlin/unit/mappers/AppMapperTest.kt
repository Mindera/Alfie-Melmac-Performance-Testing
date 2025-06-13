import mappers.AppMapper
import domain.App
import dtos.AppResponseDTO
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AppMapperTest {
    @Test
    fun `toDto maps App to AppResponseDTO correctly`() {
        val app = App(1, "MyApp")
        val dto = AppMapper.toDto(app)
        assertEquals(1, dto.appId)
        assertEquals("MyApp", dto.appName)
    }

    @Test
    fun `toDto throws if appId is null`() {
        val app = App(null, "MyApp")
        assertThrows(IllegalStateException::class.java) {
            AppMapper.toDto(app)
        }
    }

    @Test
    fun `toDomain maps AppResponseDTO to App correctly`() {
        val dto = AppResponseDTO(2, "OtherApp")
        val app = AppMapper.toDomain(dto)
        assertEquals(2, app.appId)
        assertEquals("OtherApp", app.appName)
    }
}