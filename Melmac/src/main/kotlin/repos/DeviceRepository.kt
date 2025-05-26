package repos

import domain.Device
import repos.IRepos.IDeviceRepository
import java.sql.Connection

class DeviceRepository(
    private val connection: Connection
) : IDeviceRepository {

    override fun findById(id: Int): Device? {
        val query = "SELECT DeviceID, DeviceName, DeviceSerialNumber, OSVersionOSVersionID FROM Device WHERE DeviceID = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            Device(
                deviceId = resultSet.getInt("DeviceID"),
                deviceName = resultSet.getString("DeviceName"),
                deviceSerialNumber = resultSet.getString("DeviceSerialNumber"),
                osVersionOsVersionId = resultSet.getInt("OSVersionOSVersionID")
            )
        } else null
    }

    override fun save(device: Device): Int {
        val query = "INSERT INTO Device (DeviceName, DeviceSerialNumber, OSVersionOSVersionID) VALUES (?, ?, ?)"
        val statement = connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, device.deviceName)
        statement.setString(2, device.deviceSerialNumber)
        statement.setInt(3, device.osVersionOsVersionId)
        statement.executeUpdate()

        val keys = statement.generatedKeys
        if (keys.next()) return keys.getInt(1)
        throw IllegalStateException("Failed to insert Device")
    }

    override fun findBySerialNumber(serialNumber: String): Device? {
        val query = "SELECT DeviceID, DeviceName, DeviceSerialNumber, OSVersionOSVersionID FROM Device WHERE DeviceSerialNumber = ?"
        val statement = connection.prepareStatement(query)
        statement.setString(1, serialNumber)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            Device(
                deviceId = resultSet.getInt("DeviceID"),
                deviceName = resultSet.getString("DeviceName"),
                deviceSerialNumber = resultSet.getString("DeviceSerialNumber"),
                osVersionOsVersionId = resultSet.getInt("OSVersionOSVersionID")
            )
        } else null
    }

    override fun findByName(name: String): Device? {
        val query = "SELECT DeviceID, DeviceName, DeviceSerialNumber, OSVersionOSVersionID FROM Device WHERE DeviceName = ?"
        val statement = connection.prepareStatement(query)
        statement.setString(1, name)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            Device(
                deviceId = resultSet.getInt("DeviceID"),
                deviceName = resultSet.getString("DeviceName"),
                deviceSerialNumber = resultSet.getString("DeviceSerialNumber"),
                osVersionOsVersionId = resultSet.getInt("OSVersionOSVersionID")
            )
        } else null
    }
}
