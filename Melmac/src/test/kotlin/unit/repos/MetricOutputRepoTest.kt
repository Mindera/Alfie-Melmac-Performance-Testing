import domain.MetricOutput
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.sql.*

class MetricOutputRepoTest {

    private val connection = mockk<Connection>()
    private val statement = mockk<PreparedStatement>()
    private val resultSet = mockk<ResultSet>()

    @Test
    fun `findByMetricId returns list of outputs`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returnsMany listOf(true, true, false)
        every { resultSet.getInt("MetricOutputID") } returnsMany listOf(1, 2)
        every { resultSet.getString("OutputName") } returnsMany listOf("output1", "output2")
        every { resultSet.getString("Unit") } returnsMany listOf("ms", "s")
        every { resultSet.getInt("MetricMetricID") } returnsMany listOf(10, 10)

        val repo = repos.MetricOutputRepository(connection)
        val result = repo.findByMetricId(10)
        assertEquals(2, result.size)
        assertEquals("output1", result[0].outputName)
        assertEquals("output2", result[1].outputName)
    }

    @Test
    fun `findByMetricIdAndName returns output if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 10) } just Runs
        every { statement.setString(2, "output1") } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        every { resultSet.getInt("MetricOutputID") } returns 1
        every { resultSet.getString("OutputName") } returns "output1"
        every { resultSet.getString("Unit") } returns "ms"
        every { resultSet.getInt("MetricMetricID") } returns 10

        val repo = repos.MetricOutputRepository(connection)
        val result = repo.findByMetricIdAndName(10, "output1")
        assertNotNull(result)
        assertEquals(1, result?.metricOutputId)
        assertEquals("output1", result?.outputName)
        assertEquals("ms", result?.unit)
        assertEquals(10, result?.metricMetricId)
    }

    @Test
    fun `findByMetricIdAndName returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 10) } just Runs
        every { statement.setString(2, "output1") } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.MetricOutputRepository(connection)
        val result = repo.findByMetricIdAndName(10, "output1")
        assertNull(result)
    }

    @Test
    fun `save inserts metric output and returns generated id`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(1, "output1") } just Runs
        every { statement.setString(2, "ms") } just Runs
        every { statement.setInt(3, 10) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns true
        every { keys.getInt(1) } returns 99

        val repo = repos.MetricOutputRepository(connection)
        val id = repo.save(MetricOutput(null, "output1", "ms", 10))
        assertEquals(99, id)
    }

    @Test
    fun `save throws if no generated key`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(1, "output1") } just Runs
        every { statement.setString(2, "ms") } just Runs
        every { statement.setInt(3, 10) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns false

        val repo = repos.MetricOutputRepository(connection)
        assertThrows(IllegalStateException::class.java) {
            repo.save(MetricOutput(null, "output1", "ms", 10))
        }
    }

    @Test
    fun `update updates metric output unit`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setString(1, "s") } just Runs
        every { statement.setInt(2, 5) } just Runs
        every { statement.executeUpdate() } returns 1

        val repo = repos.MetricOutputRepository(connection)
        repo.update(MetricOutput(5, "output1", "s", 10))
        // No exception means success
    }
}