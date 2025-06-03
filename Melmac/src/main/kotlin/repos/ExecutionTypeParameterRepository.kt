package repos

import domain.ExecutionTypeParameter
import java.sql.Connection
import repos.IRepos.IExecutionTypeParameterRepository

/**
 * Repository implementation for accessing and managing ExecutionTypeParameter entities in the database.
 *
 * @property connection The JDBC connection used for database operations.
 */
class ExecutionTypeParameterRepository(private val connection: Connection) :
        IExecutionTypeParameterRepository {

    /**
     * Finds an ExecutionTypeParameter by its unique identifier.
     *
     * @param id The ID of the ExecutionTypeParameter to retrieve.
     * @return The [ExecutionTypeParameter] if found, or null otherwise.
     */
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

    /**
     * Retrieves all ExecutionTypeParameter records for a given ExecutionType ID.
     *
     * @param executionTypeId The ID of the ExecutionType whose parameters are to be retrieved.
     * @return A list of [ExecutionTypeParameter] entities associated with the given ExecutionType ID.
     */
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

    /**
     * Finds an ExecutionTypeParameter by ExecutionType ID and parameter name.
     *
     * @param executionTypeId The ID of the ExecutionType.
     * @param parameterName The name of the parameter to retrieve.
     * @return The [ExecutionTypeParameter] if found, or null otherwise.
     */
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

    /**
     * Saves a new ExecutionTypeParameter to the database.
     *
     * @param executionTypeParameter The [ExecutionTypeParameter] entity to save.
     * @return The generated ID of the inserted ExecutionTypeParameter.
     * @throws IllegalStateException if the insert fails.
     */
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

    /**
     * Updates the parameter type of an existing ExecutionTypeParameter.
     *
     * @param executionTypeParameter The [ExecutionTypeParameter] entity with updated information.
     */
    override fun update(executionTypeParameter: ExecutionTypeParameter) {
        val query =
                "UPDATE ExecutionTypeParameter SET ParameterType = ? WHERE ExecutionTypeParameterID = ?"
        val statement = connection.prepareStatement(query)
        statement.setString(1, executionTypeParameter.parameterType)
        statement.setInt(2, executionTypeParameter.executionTypeParameterId!!)
        statement.executeUpdate()
    }
}