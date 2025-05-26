package repos

import domain.ExecutionTypeParameter
import java.sql.Connection
import repos.IRepos.IExecutionTypeParameterRepository

class ExecutionTypeParameterRepository(private val connection: Connection) :
        IExecutionTypeParameterRepository {

    override fun findById(id: Int): ExecutionTypeParameter? {
        val query =
                "SELECT ExecutionTypeParameterID, ParameterName, ParameterType, ExecutionTypeExecutionTypeID FROM ExecutionTypeParameter WHERE ExecutionTypeParameterID = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            ExecutionTypeParameter(
                    executionTypeParameterId = resultSet.getInt("ExecutionTypeParameterID"),
                    parameterName = resultSet.getString("ParameterName"),
                    parameterType = resultSet.getString("ParameterType"),
                    executionTypeExecutionTypeId =
                            resultSet.getInt("ExecutionTypeExecutionTypeID")
            )
        } else null
    }

    override fun findByExecutionTypeId(executionTypeId: Int): List<ExecutionTypeParameter> {
        val query =
                "SELECT ExecutionTypeParameterID, ParameterName, ParameterType, ExecutionTypeExecutionTypeID FROM ExecutionTypeParameter WHERE ExecutionTypeExecutionTypeID = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, executionTypeId)
        val resultSet = statement.executeQuery()

        val parameters = mutableListOf<ExecutionTypeParameter>()
        while (resultSet.next()) {
            parameters.add(
                    ExecutionTypeParameter(
                            executionTypeParameterId = resultSet.getInt("ExecutionTypeParameterID"),
                            parameterName = resultSet.getString("ParameterName"),
                            parameterType = resultSet.getString("ParameterType"),
                            executionTypeExecutionTypeId =
                                    resultSet.getInt("ExecutionTypeExecutionTypeID")
                    )
            )
        }
        return parameters
    }

    override fun findByExecutionTypeIdAndName(
            executionTypeId: Int,
            parameterName: String
    ): ExecutionTypeParameter? {
        val query =
                "SELECT ExecutionTypeParameterID, ParameterName, ParameterType, ExecutionTypeExecutionTypeID FROM ExecutionTypeParameter WHERE ExecutionTypeExecutionTypeID = ? AND ParameterName = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, executionTypeId)
        statement.setString(2, parameterName)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            ExecutionTypeParameter(
                    executionTypeParameterId = resultSet.getInt("ExecutionTypeParameterID"),
                    parameterName = resultSet.getString("ParameterName"),
                    parameterType = resultSet.getString("ParameterType"),
                    executionTypeExecutionTypeId = resultSet.getInt("ExecutionTypeExecutionTypeID")
            )
        } else null
    }

    override fun save(executionTypeParameter: ExecutionTypeParameter): Int {
        val query =
                "INSERT INTO ExecutionTypeParameter (ParameterName, ParameterType, ExecutionTypeExecutionTypeID) VALUES (?, ?, ?)"
        val statement = connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, executionTypeParameter.parameterName)
        statement.setString(2, executionTypeParameter.parameterType)
        statement.setInt(3, executionTypeParameter.executionTypeExecutionTypeId)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1)
        }
        throw IllegalStateException("Failed to insert ExecutionTypeParameter")
    }

    override fun update(executionTypeParameter: ExecutionTypeParameter) {
        val query =
                "UPDATE ExecutionTypeParameter SET ParameterType = ? WHERE ExecutionTypeParameterID = ?"
        val statement = connection.prepareStatement(query)
        statement.setString(1, executionTypeParameter.parameterType)
        statement.setInt(2, executionTypeParameter.executionTypeParameterId!!)
        statement.executeUpdate()
    }
}
