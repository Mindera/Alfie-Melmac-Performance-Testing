import domain.TestMetricOutputResult
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.sql.*

class TestMetricOutputResultRepoTest {

    private val connection = mockk<Connection>()
    private val statement = mockk<PreparedStatement>()
    private val resultSet = mockk<ResultSet>()

    @Test
    fun `findAll returns list of results`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returnsMany listOf(true, true, false)
        every { resultSet.getInt("TestMetricOutputResultID") } returnsMany listOf(1, 2)
        every { resultSet.getString("Value") } returnsMany listOf("123", "456")
        every { resultSet.getInt("MetricOutputMetricOutputID") } returnsMany listOf(10, 11)
        every { resultSet.getInt("TestExecutionTestExecutionID") } returnsMany listOf(100, 101)

        val repo = repos.TestMetricOutputResultRepository(connection)
        val result = repo.findAll()
        assertEquals(2, result.size)
        assertEquals(1, result[0].testMetricOutputResultId)
        assertEquals("123", result[0].value)
        assertEquals(10, result[0].metricOutputMetricOutputId)
        assertEquals(100, result[0].testExecutionTestExecutionId)
        assertEquals(2, result[1].testMetricOutputResultId)
        assertEquals("456", result[1].value)
        assertEquals(11, result[1].metricOutputMetricOutputId)
        assertEquals(101, result[1].testExecutionTestExecutionId)
    }

    @Test
    fun `getByExecutionId returns list of results for execution`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 100) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returnsMany listOf(true, false)
        every { resultSet.getInt("TestMetricOutputResultID") } returns 1
        every { resultSet.getString("Value") } returns "123"
        every { resultSet.getInt("MetricOutputMetricOutputID") } returns 10
        every { resultSet.getInt("TestExecutionTestExecutionID") } returns 100

        val repo = repos.TestMetricOutputResultRepository(connection)
        val result = repo.getByExecutionId(100)
        assertEquals(1, result.size)
        assertEquals(1, result[0].testMetricOutputResultId)
        assertEquals("123", result[0].value)
        assertEquals(10, result[0].metricOutputMetricOutputId)
        assertEquals(100, result[0].testExecutionTestExecutionId)
    }

    @Test
    fun `save inserts result and returns generated id`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(1, "789") } just Runs
        every { statement.setInt(2, 12) } just Runs
        every { statement.setInt(3, 102) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns true
        every { keys.getInt(1) } returns 99

        val repo = repos.TestMetricOutputResultRepository(connection)
        val id = repo.save(TestMetricOutputResult(null, "789", 12, 102))
        assertEquals(99, id)
    }

    @Test
    fun `save throws if no generated key`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(1, "789") } just Runs
        every { statement.setInt(2, 12) } just Runs
        every { statement.setInt(3, 102) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns false

        val repo = repos.TestMetricOutputResultRepository(connection)
        assertThrows(IllegalStateException::class.java) {
            repo.save(TestMetricOutputResult(null, "789", 12, 102))
        }
    }
}