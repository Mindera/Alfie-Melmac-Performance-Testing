package repos

import domain.OperativeSystem
import repos.IRepos.IOperSysRepository
import java.sql.Connection

/**
 * Repository implementation for accessing and managing OperativeSystem entities in the database.
 *
 * @property connection The JDBC connection used for database operations.
 */
class OperSysRepository(
    private val connection: Connection
) : IOperSysRepository {
    
    /**
     * Finds an OperativeSystem by its unique identifier.
     *
     * @param id The ID of the OperativeSystem to retrieve.
     * @return The [OperativeSystem] if found, or null otherwise.
     */
    override fun findById(id: Int): OperativeSystem? {
        val query = "SELECT OperSysID, OperSysName FROM OperativeSystem WHERE OperSysID = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            OperativeSystem(
                operSysId = resultSet.getInt("OperSysID"),
                operSysName = resultSet.getString("OperSysName")
            )
        } else null
    }

    /**
     * Finds an OperativeSystem by its name.
     *
     * @param name The name of the OperativeSystem to retrieve.
     * @return The [OperativeSystem] if found, or null otherwise.
     */
    override fun findByName(name: String): OperativeSystem? {
        val query = "SELECT OperSysID, OperSysName FROM OperativeSystem WHERE OperSysName = ?"
        val statement = connection.prepareStatement(query)
        statement.setString(1, name)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            OperativeSystem(
                operSysId = resultSet.getInt("OperSysID"),
                operSysName = resultSet.getString("OperSysName")
            )
        } else null
    }

    /**
     * Saves a new OperativeSystem to the database.
     *
     * @param operativeSystem The [OperativeSystem] entity to save.
     * @return The generated ID of the inserted OperativeSystem.
     * @throws IllegalStateException if the insert fails.
     */
    override fun save(operativeSystem: OperativeSystem): Int {
        val query = "INSERT INTO OperativeSystem (OperSysName) VALUES (?)"
        val statement = connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, operativeSystem.operSysName)
        statement.executeUpdate()

        val keys = statement.generatedKeys
        if (keys.next()) return keys.getInt(1)
        throw IllegalStateException("Failed to insert OperativeSystem")
    }
}