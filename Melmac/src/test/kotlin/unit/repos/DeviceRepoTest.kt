import domain.Device
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.sql.*

class DeviceRepoTest {

    private val connection = mockk<Connection>()
    private val statement = mockk<PreparedStatement>()
    private val resultSet = mockk<ResultSet>()

    @Test
    fun `findById returns device if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        every { resultSet.getInt("DeviceID") } returns 1
        every { resultSet.getString("DeviceName") } returns "Pixel"
        every { resultSet.getString("DeviceSerialNumber") } returns "ABC123"
        every { resultSet.getInt("OSVersionOSVersionID") } returns 42

        val repo = repos.DeviceRepository(connection)
        val result = repo.findById(1)
        assertNotNull(result)
        assertEquals(1, result?.deviceId)
        assertEquals("Pixel", result?.deviceName)
        assertEquals("ABC123", result?.deviceSerialNumber)
        assertEquals(42, result?.osVersionOsVersionId)
    }

    @Test
    fun `findById returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.DeviceRepository(connection)
        val result = repo.findById(1)
        assertNull(result)
    }

    @Test
    fun `save inserts device and returns generated id`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(1, "Pixel") } just Runs
        every { statement.setString(2, "ABC123") } just Runs
        every { statement.setInt(3, 42) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns true
        every { keys.getInt(1) } returns 99

        val repo = repos.DeviceRepository(connection)
        val id = repo.save(Device(null, "Pixel", "ABC123", 42))
        assertEquals(99, id)
    }

    @Test
    fun `save throws if no generated key`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(1, "Pixel") } just Runs
        every { statement.setString(2, "ABC123") } just Runs
        every { statement.setInt(3, 42) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns false

        val repo = repos.DeviceRepository(connection)
        assertThrows(IllegalStateException::class.java) {
            repo.save(Device(null, "Pixel", "ABC123", 42))
        }
    }

    @Test
    fun `findBySerialNumber returns device if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setString(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        every { resultSet.getInt("DeviceID") } returns 2
        every { resultSet.getString("DeviceName") } returns "iPhone"
        every { resultSet.getString("DeviceSerialNumber") } returns "XYZ789"
        every { resultSet.getInt("OSVersionOSVersionID") } returns 43

        val repo = repos.DeviceRepository(connection)
        val result = repo.findBySerialNumber("XYZ789")
        assertNotNull(result)
        assertEquals(2, result?.deviceId)
        assertEquals("iPhone", result?.deviceName)
        assertEquals("XYZ789", result?.deviceSerialNumber)
        assertEquals(43, result?.osVersionOsVersionId)
    }

    @Test
    fun `findBySerialNumber returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setString(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.DeviceRepository(connection)
        val result = repo.findBySerialNumber("XYZ789")
        assertNull(result)
    }

    @Test
    fun `findByName returns device if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setString(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        every { resultSet.getInt("DeviceID") } returns 3
        every { resultSet.getString("DeviceName") } returns "Galaxy"
        every { resultSet.getString("DeviceSerialNumber") } returns "SERIAL3"
        every { resultSet.getInt("OSVersionOSVersionID") } returns 44

        val repo = repos.DeviceRepository(connection)
        val result = repo.findByName("Galaxy")
        assertNotNull(result)
        assertEquals(3, result?.deviceId)
        assertEquals("Galaxy", result?.deviceName)
        assertEquals("SERIAL3", result?.deviceSerialNumber)
        assertEquals(44, result?.osVersionOsVersionId)
    }

    @Test
    fun `findByName returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setString(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.DeviceRepository(connection)
        val result = repo.findByName("Galaxy")
        assertNull(result)
    }
}