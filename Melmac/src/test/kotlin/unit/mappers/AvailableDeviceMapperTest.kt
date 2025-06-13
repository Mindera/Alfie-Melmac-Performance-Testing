import mappers.AvailableDeviceMapper
import dtos.AvailableDeviceDTO
import domain.Device
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AvailableDeviceMapperTest {
    @Test
    fun `toDto maps Device to AvailableDeviceDTO correctly`() {
        val device = Device(1, "Pixel 5", "ABC123", 10)
        val dto = AvailableDeviceMapper.toDto(device, "Android", "12")
        assertEquals(1, dto.id)
        assertEquals("Pixel 5", dto.deviceName)
        assertEquals("ABC123", dto.deviceSerialNumber)
        assertEquals("Android", dto.osName)
        assertEquals("12", dto.osVersion)
    }

    @Test
    fun `toDomain maps AvailableDeviceDTO to Device correctly`() {
        val dto = AvailableDeviceDTO(2, "iPhone", "XYZ789", "iOS", "16")
        val device = AvailableDeviceMapper.toDomain(dto, 99)
        assertEquals(2, device.deviceId)
        assertEquals("iPhone", device.deviceName)
        assertEquals("XYZ789", device.deviceSerialNumber)
        assertEquals(99, device.osVersionOsVersionId)
    }
}