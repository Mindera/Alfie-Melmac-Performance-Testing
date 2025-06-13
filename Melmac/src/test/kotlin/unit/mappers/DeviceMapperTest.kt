import mappers.DeviceMapper
import domain.Device
import dtos.DeviceResponseDTO
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DeviceMapperTest {
    @Test
    fun `toDto maps Device to DeviceResponseDTO correctly`() {
        val device = Device(1, "Pixel 5", "ABC123", 10)
        val dto = DeviceMapper.toDto(device)
        assertEquals(1, dto.deviceId)
        assertEquals("Pixel 5", dto.deviceName)
        assertEquals("ABC123", dto.deviceSerialNumber)
        assertEquals(10, dto.osVersionOsVersionId)
    }

    @Test
    fun `toDto throws if deviceId is null`() {
        val device = Device(null, "Pixel 5", "ABC123", 10)
        assertThrows(IllegalStateException::class.java) {
            DeviceMapper.toDto(device)
        }
    }

    @Test
    fun `toDomain maps DeviceResponseDTO to Device correctly`() {
        val dto = DeviceResponseDTO(2, "iPhone", "XYZ789", 99)
        val device = DeviceMapper.toDomain(dto)
        assertEquals(2, device.deviceId)
        assertEquals("iPhone", device.deviceName)
        assertEquals("XYZ789", device.deviceSerialNumber)
        assertEquals(99, device.osVersionOsVersionId)
    }
}