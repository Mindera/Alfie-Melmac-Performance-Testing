package repos

import domain.Device
import repos.IRepos.IDeviceRepository
import java.sql.Connection

/**
 * Repository implementation for accessing Device entities from the database.
 *
 * @property connection The JDBC connection used for database operations.
 */
class DeviceRepository(
    private val connection: Connection
) : IDeviceRepository {

    /**
     * Finds a Device by its unique identifier.
     *
     * @param id The ID of the Device to retrieve.
     * @return The [Device] if found, or null otherwise.
     */
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

    /**
     * Saves a new Device to the database.
     *
     * @param device The [Device] entity to save.
     * @return The generated ID of the inserted Device.
     * @throws IllegalStateException if the insert fails.
     */
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

    /**
     * Finds a Device by its serial number.
     *
     * @param serialNumber The serial number of the Device to retrieve.
     * @return The [Device] if found, or null otherwise.
     */
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

    /**
     * Finds a Device by its name.
     *
     * @param name The name of the Device to retrieve.
     * @return The [Device] if found, or null otherwise.
     */
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