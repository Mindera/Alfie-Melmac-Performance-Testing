package repos

import domain.AppVersion
import repos.IRepos.IAppVersionRepository
import java.sql.Connection

/**
 * Repository implementation for accessing AppVersion entities from the database.
 *
 * @property connection The JDBC connection used for database operations.
 */
class AppVersionRepository(
    private val connection: Connection
) : IAppVersionRepository {

    /**
     * Retrieves all AppVersion records for a given App ID.
     *
     * @param appId The ID of the App whose versions are to be retrieved.
     * @return A list of [AppVersion] entities associated with the given App ID.
     */
    override fun findByAppId(appId: Int): List<AppVersion> {
        val query = "SELECT AppVersionID, Version, AppAppID FROM AppVersion WHERE AppAppID = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, appId)
        val resultSet = statement.executeQuery()

        val list = mutableListOf<AppVersion>()
        while (resultSet.next()) {
            list.add(
                AppVersion(
                    appVersionId = resultSet.getInt("AppVersionID"),
                    appId = resultSet.getInt("AppAppID"),
                    appVersion = resultSet.getString("Version")
                )
            )
        }
        return list
    }

    /**
     * Finds an AppVersion by its unique identifier.
     *
     * @param id The ID of the AppVersion to retrieve.
     * @return The [AppVersion] if found, or null otherwise.
     */
    override fun findById(id: Int): AppVersion? {
        val query = "SELECT AppVersionID, Version, AppAppID FROM AppVersion WHERE AppVersionID = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            AppVersion(
                appVersionId = resultSet.getInt("AppVersionID"),
                appId = resultSet.getInt("AppAppID"),
                appVersion = resultSet.getString("Version")
            )
        } else null
    }

    /**
     * Finds an AppVersion by its version name.
     *
     * @param appVersion The version name of the AppVersion to retrieve.
     * @return The [AppVersion] if found, or null otherwise.
     */
    override fun findByName(appVersion: String): AppVersion? {
        val query = "SELECT AppVersionID, Version, AppAppID FROM AppVersion WHERE Version = ?"
        val statement = connection.prepareStatement(query)
        statement.setString(1, appVersion)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            AppVersion(
                appVersionId = resultSet.getInt("AppVersionID"),
                appId = resultSet.getInt("AppAppID"),
                appVersion = resultSet.getString("Version")
            )
        } else null
    }

    /**
     * Finds an AppVersion by App ID and version name.
     *
     * @param appId The ID of the App.
     * @param version The version name to search for.
     * @return The [AppVersion] if found, or null otherwise.
     */
    override fun findByAppIdAndVersion(appId: Int, version: String): AppVersion? {
        val query = "SELECT AppVersionID, Version, AppAppID FROM AppVersion WHERE AppAppID = ? AND Version = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, appId)
        statement.setString(2, version)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            AppVersion(
                appVersionId = resultSet.getInt("AppVersionID"),
                appId = resultSet.getInt("AppAppID"),
                appVersion = resultSet.getString("Version")
            )
        } else null
    }

    /**
     * Saves a new AppVersion to the database.
     *
     * @param appVersion The [AppVersion] entity to save.
     * @return The generated ID of the inserted AppVersion.
     * @throws IllegalStateException if the insert fails.
     */
    override fun save(appVersion: AppVersion): Int {
        val query = "INSERT INTO AppVersion (Version, AppAppID) VALUES (?, ?)"
        val statement = connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, appVersion.appVersion)
        statement.setInt(2, appVersion.appId)
        statement.executeUpdate()

        val keys = statement.generatedKeys
        if (keys.next()) return keys.getInt(1)
        throw IllegalStateException("Failed to insert AppVersion")
    }
}