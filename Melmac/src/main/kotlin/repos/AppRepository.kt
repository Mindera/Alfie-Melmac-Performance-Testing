package repos

import domain.App
import repos.IRepos.IAppRepository
import java.sql.Connection

/**
 * Repository implementation for accessing App entities from the database.
 *
 * @property connection The JDBC connection used for database operations.
 */
class AppRepository(
    private val connection: Connection
) : IAppRepository {

    /**
     * Retrieves all App records from the database.
     *
     * @return A list of all [App] entities.
     */
    override fun findAll(): List<App> {
        val query = "SELECT AppID, AppName FROM App"
        val statement = connection.prepareStatement(query)
        val resultSet = statement.executeQuery()

        val apps = mutableListOf<App>()
        while (resultSet.next()) {
            apps.add(
                App(
                    appId = resultSet.getInt("AppID"),
                    appName = resultSet.getString("AppName")
                )
            )
        }
        return apps
    }

    /**
     * Finds an App by its unique identifier.
     *
     * @param appId The ID of the App to retrieve.
     * @return The [App] if found, or null otherwise.
     */
    override fun findById(appId: Int): App? {
        val query = "SELECT AppID, AppName FROM App WHERE AppID = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, appId)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            App(
                appId = resultSet.getInt("AppID"),
                appName = resultSet.getString("AppName")
            )
        } else null
    }

    /**
     * Finds an App by its name.
     *
     * @param appName The name of the App to retrieve.
     * @return The [App] if found, or null otherwise.
     */
    override fun findByName(appName: String): App? {
        val query = "SELECT AppID, AppName FROM App WHERE AppName = ?"
        val statement = connection.prepareStatement(query)
        statement.setString(1, appName)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            App(
                appId = resultSet.getInt("AppID"),
                appName = resultSet.getString("AppName")
            )
        } else null
    }

    /**
     * Saves a new App to the database.
     *
     * @param app The [App] entity to save.
     * @return The generated ID of the inserted App.
     * @throws IllegalStateException if the insert fails.
     */
    override fun save(app: App): Int {
        val query = "INSERT INTO App (AppName) VALUES (?)"
        val statement = connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, app.appName)
        statement.executeUpdate()

        val keys = statement.generatedKeys
        if (keys.next()) return keys.getInt(1)
        throw IllegalStateException("Failed to insert App")
    }
}