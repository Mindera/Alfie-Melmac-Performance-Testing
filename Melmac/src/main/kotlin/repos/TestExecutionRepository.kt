package repos

import domain.TestExecution
import repos.IRepos.ITestExecutionRepository
import java.sql.Connection
import java.sql.Timestamp

/**
 * Repository implementation for accessing and managing TestExecution entities in the database.
 *
 * @property connection The JDBC connection used for database operations.
 */
class TestExecutionRepository(
    private val connection: Connection
) : ITestExecutionRepository {

    /**
     * Retrieves all TestExecution records from the database.
     *
     * @return A list of all [TestExecution] entities.
     */
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

    /**
     * Finds a TestExecution by its unique identifier.
     *
     * @param id The ID of the TestExecution to retrieve.
     * @return The [TestExecution] if found, or null otherwise.
     */
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

    /**
     * Saves a new TestExecution to the database.
     *
     * @param testExecution The [TestExecution] entity to save.
     * @return The generated ID of the inserted TestExecution.
     * @throws IllegalStateException if the insert fails.
     */
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