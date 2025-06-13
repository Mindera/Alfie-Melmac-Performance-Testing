import domain.TestMetricParameter
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.sql.*

class TestPlanMetricParameterRepoTest {

    private val connection = mockk<Connection>()
    private val statement = mockk<PreparedStatement>(relaxed = true)
    private val resultSet = mockk<ResultSet>(relaxed = true)

    @Test
    fun `findByTestPlanVersionId returns list of parameters`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 7) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returnsMany listOf(true, false)
        every { resultSet.getInt("TestMetricParameterID") } returns 1
        every { resultSet.getString("ParameterValue") } returns "val"
        every { resultSet.getInt("MetricParameterMetricParameterID") } returns 2
        every { resultSet.getInt("TestPlanVersionTestPlanVersionID") } returns 7

        val repo = repos.TestPlanMetricParameterValueRepository(connection)
        val result = repo.findByTestPlanVersionId(7)
        assertEquals(1, result.size)
        assertEquals(1, result[0].testMetricParameterId)
        assertEquals("val", result[0].parameterValue)
        assertEquals(2, result[0].metricParameterMetricParameterId)
        assertEquals(7, result[0].testPlanVersionTestPlanVersionId)
    }

    @Test
    fun `save inserts parameter and returns generated id`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(1, "val") } just Runs
        every { statement.setInt(2, 2) } just Runs
        every { statement.setInt(3, 7) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns true
        every { keys.getInt(1) } returns 99
        every { keys.close() } just Runs

        val repo = repos.TestPlanMetricParameterValueRepository(connection)
        val id = repo.save(TestMetricParameter(null, "val", 2, 7))
        assertEquals(99, id)
    }

    @Test
    fun `save throws if no generated key`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(1, "val") } just Runs
        every { statement.setInt(2, 2) } just Runs
        every { statement.setInt(3, 7) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns false

        val repo = repos.TestPlanMetricParameterValueRepository(connection)
        assertThrows(Exception::class.java) {
            repo.save(TestMetricParameter(null, "val", 2, 7))
        }
    }
}