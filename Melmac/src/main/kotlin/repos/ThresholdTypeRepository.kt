package repos

import domain.ThresholdType
import java.sql.Connection
import repos.IRepos.IThresholdTypeRepository

/**
 * Repository implementation for accessing and managing ThresholdType entities in the database.
 *
 * @property connection The JDBC connection used for database operations.
 */
class ThresholdTypeRepository(private val connection: Connection) : IThresholdTypeRepository {

    /**
     * Finds a ThresholdType by its unique identifier.
     *
     * @param thresholdTypeId The ID of the ThresholdType to retrieve.
     * @return The [ThresholdType] if found, or null otherwise.
     */
    override fun findById(thresholdTypeId: Int): ThresholdType? {
        val query =
                "SELECT ThresholdTypeID, ThresholdTypeName, ThresholdTypeDescription FROM ThresholdType WHERE ThresholdTypeID = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, thresholdTypeId)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            ThresholdType(
                    thresholdTypeId = resultSet.getInt("ThresholdTypeID"),
                    thresholdTypeName = resultSet.getString("ThresholdTypeName"),
                    thresholdTypeDescription = resultSet.getString("ThresholdTypeDescription")
            )
        } else null
    }

    /**
     * Retrieves all ThresholdType records from the database.
     *
     * @return A list of all [ThresholdType] entities.
     */
    override fun findAll(): List<ThresholdType> {
        val query =
                "SELECT ThresholdTypeID, ThresholdTypeName, ThresholdTypeDescription FROM ThresholdType"
        val statement = connection.prepareStatement(query)
        val resultSet = statement.executeQuery()

        val list = mutableListOf<ThresholdType>()
        while (resultSet.next()) {
            list.add(
                    ThresholdType(
                            thresholdTypeId = resultSet.getInt("ThresholdTypeID"),
                            thresholdTypeName = resultSet.getString("ThresholdTypeName"),
                            thresholdTypeDescription =
                                    resultSet.getString("ThresholdTypeDescription")
                    )
            )
        }
        return list
    }

    /**
     * Finds a ThresholdType by its name.
     *
     * @param name The name of the ThresholdType to retrieve.
     * @return The [ThresholdType] if found, or null otherwise.
     */
    override fun findByName(name: String): ThresholdType? {
        val query =
                "SELECT ThresholdTypeID, ThresholdTypeName, ThresholdTypeDescription FROM ThresholdType WHERE LOWER(ThresholdTypeName) = LOWER(?)"
        val statement = connection.prepareStatement(query)
        statement.setString(1, name)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            ThresholdType(
                    thresholdTypeId = resultSet.getInt("ThresholdTypeID"),
                    thresholdTypeName = resultSet.getString("ThresholdTypeName"),
                    thresholdTypeDescription = resultSet.getString("ThresholdTypeDescription")
            )
        } else null
    }

    /**
     * Saves a new ThresholdType to the database.
     *
     * @param thresholdType The [ThresholdType] entity to save.
     * @return The generated ID of the inserted ThresholdType.
     * @throws IllegalStateException if the insert fails.
     */
    override fun save(thresholdType: ThresholdType): Int {
        val query =
                "INSERT INTO ThresholdType (ThresholdTypeName, ThresholdTypeDescription) VALUES (?, ?)"
        val statement = connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, thresholdType.thresholdTypeName)
        statement.setString(2, thresholdType.thresholdTypeDescription)
        statement.executeUpdate()

        val keys = statement.generatedKeys
        if (keys.next()) return keys.getInt(1)
        throw IllegalStateException("Failed to insert ThresholdType")
    }

    /**
     * Updates an existing ThresholdType in the database.
     *
     * @param thresholdType The [ThresholdType] entity with updated information.
     * @return The number of rows affected by the update.
     */
    override fun update(thresholdType: ThresholdType): Int {
        val query =
                "UPDATE ThresholdType SET ThresholdTypeName = ?, ThresholdTypeDescription = ? WHERE ThresholdTypeID = ?"
        val statement = connection.prepareStatement(query)
        statement.setString(1, thresholdType.thresholdTypeName)
        statement.setString(2, thresholdType.thresholdTypeDescription)
        statement.setInt(3, thresholdType.thresholdTypeId!!)
        return statement.executeUpdate()
    }
}