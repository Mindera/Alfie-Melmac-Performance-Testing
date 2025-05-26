package repos

import domain.App
import repos.IRepos.IAppRepository
import java.sql.Connection

class AppRepository(
    private val connection: Connection
) : IAppRepository {

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
