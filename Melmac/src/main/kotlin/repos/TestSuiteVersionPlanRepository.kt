package repos

import domain.TestSuiteVersionPlan
import repos.IRepos.ITestSuiteVersionPlanRepository
import java.sql.Connection

/**
 * Repository implementation for accessing and managing TestSuiteVersionPlan entities in the database.
 *
 * @property connection The JDBC connection used for database operations.
 */
class TestSuiteVersionPlanRepository(
    private val connection: Connection
) : ITestSuiteVersionPlanRepository {

    /**
     * Finds a TestSuiteVersionPlan by its unique identifier.
     *
     * @param id The ID of the TestSuiteVersionPlan to retrieve.
     * @return The [TestSuiteVersionPlan] if found, or null otherwise.
     */
    override fun findById(id: Int): TestSuiteVersionPlan? {
        val query = "SELECT TestSuiteVersionTestSuiteVersionID, TestPlanVersionTestPlanVersionID, \"Order\" FROM TestSuiteVersionPlan WHERE TestSuiteVersionTestSuiteVersionID = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            TestSuiteVersionPlan(
                testSuiteVersionTestSuiteVersionId = resultSet.getInt("TestSuiteVersionTestSuiteVersionID"),
                testPlanVersionTestPlanVersionId = resultSet.getInt("TestPlanVersionTestPlanVersionID"),
                order = resultSet.getInt("Order")
            )
        } else null
    }

    /**
     * Retrieves all TestSuiteVersionPlan records for a given TestSuiteVersion ID, ordered by "Order".
     *
     * @param versionId The ID of the TestSuiteVersion whose plans are to be retrieved.
     * @return A list of [TestSuiteVersionPlan] entities associated with the given TestSuiteVersion ID.
     */
    override fun findByTestSuiteVersionId(versionId: Int): List<TestSuiteVersionPlan> {
        val query = """
            SELECT TestSuiteVersionTestSuiteVersionID, TestPlanVersionTestPlanVersionID, "Order"
            FROM TestSuiteVersionPlan
            WHERE TestSuiteVersionTestSuiteVersionID = ?
            ORDER BY "Order"
        """
        val statement = connection.prepareStatement(query)
        statement.setInt(1, versionId)
        val resultSet = statement.executeQuery()

        val list = mutableListOf<TestSuiteVersionPlan>()
        while (resultSet.next()) {
            list.add(
                TestSuiteVersionPlan(
                    testSuiteVersionTestSuiteVersionId = resultSet.getInt("TestSuiteVersionTestSuiteVersionID"),
                    testPlanVersionTestPlanVersionId = resultSet.getInt("TestPlanVersionTestPlanVersionID"),
                    order = resultSet.getInt("Order")
                )
            )
        }
        return list
    }

    /**
     * Retrieves all TestSuiteVersionPlan records for a given TestPlanVersion ID, ordered by TestSuiteVersionTestSuiteVersionID descending.
     *
     * @param versionId The ID of the TestPlanVersion whose plans are to be retrieved.
     * @return A list of [TestSuiteVersionPlan] entities associated with the given TestPlanVersion ID.
     */
    override fun findByTestPlanVersionId(versionId: Int): List<TestSuiteVersionPlan> {
        val query = """
            SELECT TestSuiteVersionTestSuiteVersionID, TestPlanVersionTestPlanVersionID, "Order"
            FROM TestSuiteVersionPlan
            WHERE TestPlanVersionTestPlanVersionID = ?
            ORDER BY TestSuiteVersionTestSuiteVersionID DESC
        """
        val statement = connection.prepareStatement(query)
        statement.setInt(1, versionId)
        val resultSet = statement.executeQuery()

        val list = mutableListOf<TestSuiteVersionPlan>()
        while (resultSet.next()) {
            list.add(
                TestSuiteVersionPlan(
                    testSuiteVersionTestSuiteVersionId = resultSet.getInt("TestSuiteVersionTestSuiteVersionID"),
                    testPlanVersionTestPlanVersionId = resultSet.getInt("TestPlanVersionTestPlanVersionID"),
                    order = resultSet.getInt("Order")
                )
            )
        }
        return list
    }

    /**
     * Saves a new TestSuiteVersionPlan to the database.
     *
     * @param testSuiteVersionPlan The [TestSuiteVersionPlan] entity to save.
     * @return The generated ID of the inserted TestSuiteVersionPlan.
     * @throws IllegalStateException if the insert fails.
     */
    override fun save(testSuiteVersionPlan: TestSuiteVersionPlan): Int {
        val query = """
            INSERT INTO TestSuiteVersionPlan (TestSuiteVersionTestSuiteVersionID, TestPlanVersionTestPlanVersionID, "Order")
            VALUES (?, ?, ?)
        """
        val statement = connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)
        statement.setInt(1, testSuiteVersionPlan.testSuiteVersionTestSuiteVersionId)
        statement.setInt(2, testSuiteVersionPlan.testPlanVersionTestPlanVersionId)
        statement.setInt(3, testSuiteVersionPlan.order)
        statement.executeUpdate()

        val keys = statement.generatedKeys
        if (keys.next()) return keys.getInt(1)
        throw IllegalStateException("Failed to insert TestSuiteVersionPlan")
    }
}