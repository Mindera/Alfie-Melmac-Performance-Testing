package repos

import domain.TestSuiteVersionPlan
import repos.IRepos.ITestSuiteVersionPlanRepository
import java.sql.Connection

class TestSuiteVersionPlanRepository(
    private val connection: Connection
) : ITestSuiteVersionPlanRepository {

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
