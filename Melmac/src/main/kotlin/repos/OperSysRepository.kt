package repos

import domain.OperativeSystem
import repos.IRepos.IOperSysRepository
import java.sql.Connection

class OperSysRepository(
    private val connection: Connection
) : IOperSysRepository {
    
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
