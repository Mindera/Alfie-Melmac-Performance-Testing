package repos

import domain.OSVersion
import repos.IRepos.IOperSysVersionRepository
import java.sql.Connection

/**
 * Repository implementation for accessing and managing OSVersion entities in the database.
 *
 * @property connection The JDBC connection used for database operations.
 */
class OperSysVersionRepository(
    private val connection: Connection
) : IOperSysVersionRepository {

    /**
     * Finds an OSVersion by its unique identifier.
     *
     * @param id The ID of the OSVersion to retrieve.
     * @return The [OSVersion] if found, or null otherwise.
     */
    override fun findById(id: Int): OSVersion? {
        val query = "SELECT OSVersionID, Version, OperativeSystemOperSysID FROM OSVersion WHERE OSVersionID = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            OSVersion(
                osVersionId = resultSet.getInt("OSVersionID"),
                version = resultSet.getString("Version"),
                operativeSystemOperSysId = resultSet.getInt("OperativeSystemOperSysID")
            )
        } else null
    }

    /**
     * Retrieves all OSVersion records for a given OperativeSystem ID.
     *
     * @param operSysId The ID of the OperativeSystem whose versions are to be retrieved.
     * @return A list of [OSVersion] entities associated with the given OperativeSystem ID.
     */
    override fun findByOperSysId(operSysId: Int): List<OSVersion> {
        val query = "SELECT OSVersionID, Version, OperativeSystemOperSysID FROM OSVersion WHERE OperativeSystemOperSysID = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, operSysId)
        val resultSet = statement.executeQuery()

        val list = mutableListOf<OSVersion>()
        while (resultSet.next()) {
            list.add(
                OSVersion(
                    osVersionId = resultSet.getInt("OSVersionID"),
                    version = resultSet.getString("Version"),
                    operativeSystemOperSysId = resultSet.getInt("OperativeSystemOperSysID")
                )
            )
        }
        return list
    }

    /**
     * Saves a new OSVersion to the database.
     *
     * @param osVersion The [OSVersion] entity to save.
     * @return The generated ID of the inserted OSVersion.
     * @throws IllegalStateException if the insert fails.
     */
    override fun save(osVersion: OSVersion): Int {
        val query = "INSERT INTO OSVersion (Version, OperativeSystemOperSysID) VALUES (?, ?)"
        val statement = connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, osVersion.version)
        statement.setInt(2, osVersion.operativeSystemOperSysId)
        statement.executeUpdate()

        val keys = statement.generatedKeys
        if (keys.next()) return keys.getInt(1)
        throw IllegalStateException("Failed to insert OSVersion")
    }
}