package repos

import domain.TestExecution
import repos.IRepos.ITestExecutionRepository
import java.sql.Connection
import java.sql.Timestamp

class TestExecutionRepository(
    private val connection: Connection
) : ITestExecutionRepository {

    override fun findAll(): List<TestExecution> {
        val query = """
            SELECT TestExecutionID, InitialTimestamp, EndTimestamp, Passed, TestPlanVersionTestPlanVersionID
            FROM TestExecution
        """
        val statement = connection.prepareStatement(query)
        val resultSet = statement.executeQuery()

        val executions = mutableListOf<TestExecution>()
        while (resultSet.next()) {
            executions.add(
                TestExecution(
                    testExecutionId = resultSet.getInt("TestExecutionID"),
                    initialTimestamp = resultSet.getTimestamp("InitialTimestamp").toLocalDateTime(),
                    endTimestamp = resultSet.getTimestamp("EndTimestamp").toLocalDateTime(),
                    passed = resultSet.getString("Passed"),
                    testPlanVersionTestPlanVersionId = resultSet.getInt("TestPlanVersionTestPlanVersionID")
                )
            )
        }
        return executions
    }

    override fun findById(id: Int): TestExecution? {
        val query = """
            SELECT TestExecutionID, InitialTimestamp, EndTimestamp, Passed, TestPlanVersionTestPlanVersionID
            FROM TestExecution WHERE TestExecutionID = ?
        """
        val statement = connection.prepareStatement(query)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            TestExecution(
                testExecutionId = resultSet.getInt("TestExecutionID"),
                initialTimestamp = resultSet.getTimestamp("InitialTimestamp").toLocalDateTime(),
                endTimestamp = resultSet.getTimestamp("EndTimestamp").toLocalDateTime(),
                passed = resultSet.getString("Passed"),
                testPlanVersionTestPlanVersionId = resultSet.getInt("TestPlanVersionTestPlanVersionID")
            )
        } else null
    }

    override fun save(testExecution: TestExecution): Int {
        val query = """
            INSERT INTO TestExecution (InitialTimestamp, EndTimestamp, Passed, TestPlanVersionTestPlanVersionID)
            VALUES (?, ?, ?, ?)
        """
        val statement = connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)
        statement.setTimestamp(1, Timestamp.valueOf(testExecution.initialTimestamp))
        statement.setTimestamp(2, Timestamp.valueOf(testExecution.endTimestamp))
        statement.setBoolean(3, testExecution.passed.toBoolean())
        statement.setInt(4, testExecution.testPlanVersionTestPlanVersionId)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1)
        }
        throw IllegalStateException("Failed to insert TestExecution")
    }
}
