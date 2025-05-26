package repos

import domain.ThresholdType
import java.sql.Connection
import repos.IRepos.IThresholdTypeRepository

class ThresholdTypeRepository(private val connection: Connection) : IThresholdTypeRepository {

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

    override fun findByName(name: String): ThresholdType? {
        val query =
                "SELECT ThresholdTypeID, ThresholdTypeName, ThresholdTypeDescription FROM ThresholdType WHERE ThresholdTypeName = ?"
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
