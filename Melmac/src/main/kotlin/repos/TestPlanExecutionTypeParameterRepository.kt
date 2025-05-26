package repos

import domain.TestExecutionTypeParameter
import repos.IRepos.ITestPlanExecutionTypeParameterValueRepository
import java.sql.Connection

class TestPlanExecutionTypeParameterValueRepository(
    private val connection: Connection
) : ITestPlanExecutionTypeParameterValueRepository {

    override fun findByTestPlanVersionId(testPlanVersionId: Int): List<TestExecutionTypeParameter> {
        val query = """
            SELECT TestExecutionTypeParameterID, ParameterValue, ExecutionTypeParameterExecutionTypeParameterID, TestPlanVersionTestPlanVersionID
            FROM TestExecutionTypeParameter
            WHERE TestPlanVersionTestPlanVersionID = ?
        """
        val statement = connection.prepareStatement(query)
        statement.setInt(1, testPlanVersionId)
        val resultSet = statement.executeQuery()

        val params = mutableListOf<TestExecutionTypeParameter>()
        while (resultSet.next()) {
            params.add(
                TestExecutionTypeParameter(
                    testExecutionTypeParameterId = resultSet.getInt("TestExecutionTypeParameterID"),
                    parameterValue = resultSet.getString("ParameterValue"),
                    executionTypeParameterExecutionTypeParameterId = resultSet.getInt("ExecutionTypeParameterExecutionTypeParameterID"),
                    testPlanVersionTestPlanVersionId = resultSet.getInt("TestPlanVersionTestPlanVersionID")
                )
            )
        }
        return params
    }

    override fun save(testExecutionTypeParameter: TestExecutionTypeParameter): Int {
        val query = """
            INSERT INTO TestExecutionTypeParameter (ParameterValue, ExecutionTypeParameterExecutionTypeParameterID, TestPlanVersionTestPlanVersionID)
            VALUES (?, ?, ?)
        """
        connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS).use { statement ->
            statement.setString(1, testExecutionTypeParameter.parameterValue)
            statement.setInt(2, testExecutionTypeParameter.executionTypeParameterExecutionTypeParameterId)
            statement.setInt(3, testExecutionTypeParameter.testPlanVersionTestPlanVersionId)
            statement.executeUpdate()
            statement.generatedKeys.use { resultSet ->
                return if (resultSet.next()) {
                    resultSet.getInt(1)
                } else {
                    throw Exception("Failed to retrieve generated key for TestExecutionTypeParameter")
                }
            }
        }
    }
}
