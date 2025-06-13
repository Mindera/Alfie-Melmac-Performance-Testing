import domain.TestPlan
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.sql.*

class TestPlanRepoTest {

    private val connection = mockk<Connection>()
    private val statement = mockk<PreparedStatement>(relaxed = true)
    private val resultSet = mockk<ResultSet>(relaxed = true)

    @Test
    fun `findById returns test plan if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 5) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        every { resultSet.getInt("TestPlanID") } returns 5
        every { resultSet.getString("TestName") } returns "PlanA"
        every { resultSet.getInt("MetricMetricID") } returns 10

        val repo = repos.TestPlanRepository(connection)
        val result = repo.findById(5)
        assertNotNull(result)
        assertEquals(5, result?.testPlanId)
        assertEquals("PlanA", result?.testName)
        assertEquals(10, result?.metricMetricId)
    }

    @Test
    fun `findById returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 5) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.TestPlanRepository(connection)
        val result = repo.findById(5)
        assertNull(result)
    }

    @Test
    fun `save inserts test plan and returns generated id`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(1, "PlanB") } just Runs
        every { statement.setInt(2, 11) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns true
        every { keys.getInt(1) } returns 99
        every { keys.close() } just Runs

        val repo = repos.TestPlanRepository(connection)
        val id = repo.save(TestPlan(null, "PlanB", 11))
        assertEquals(99, id)
    }

    @Test
    fun `save throws if no generated key`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(1, "PlanB") } just Runs
        every { statement.setInt(2, 11) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns false

        val repo = repos.TestPlanRepository(connection)
        assertThrows(Exception::class.java) {
            repo.save(TestPlan(null, "PlanB", 11))
        }
    }
}