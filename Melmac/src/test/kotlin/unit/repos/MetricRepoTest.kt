import domain.Metric
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.sql.*

class MetricRepoTest {

    private val connection = mockk<Connection>()
    private val statement = mockk<PreparedStatement>()
    private val resultSet = mockk<ResultSet>()

    @Test
    fun `findAll returns list of metrics`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returnsMany listOf(true, true, false)
        every { resultSet.getInt("MetricID") } returnsMany listOf(1, 2)
        every { resultSet.getString("MetricName") } returnsMany listOf("metric1", "metric2")

        val repo = repos.MetricRepository(connection)
        val result = repo.findAll()
        assertEquals(2, result.size)
        assertEquals("metric1", result[0].metricName)
        assertEquals("metric2", result[1].metricName)
    }

    @Test
    fun `findById returns metric if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        every { resultSet.getInt("MetricID") } returns 1
        every { resultSet.getString("MetricName") } returns "metric1"

        val repo = repos.MetricRepository(connection)
        val result = repo.findById(1)
        assertNotNull(result)
        assertEquals(1, result?.metricId)
        assertEquals("metric1", result?.metricName)
    }

    @Test
    fun `findById returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.MetricRepository(connection)
        val result = repo.findById(1)
        assertNull(result)
    }

    @Test
    fun `findByName returns metric if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setString(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        every { resultSet.getInt("MetricID") } returns 2
        every { resultSet.getString("MetricName") } returns "metric2"

        val repo = repos.MetricRepository(connection)
        val result = repo.findByName("metric2")
        assertNotNull(result)
        assertEquals(2, result?.metricId)
        assertEquals("metric2", result?.metricName)
    }

    @Test
    fun `findByName returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setString(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.MetricRepository(connection)
        val result = repo.findByName("notfound")
        assertNull(result)
    }

    @Test
    fun `save inserts metric and returns generated id`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(1, "metric3") } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns true
        every { keys.getInt(1) } returns 99

        val repo = repos.MetricRepository(connection)
        val id = repo.save(Metric(null, "metric3"))
        assertEquals(99, id)
    }

    @Test
    fun `save throws if no generated key`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(1, "metric3") } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns false

        val repo = repos.MetricRepository(connection)
        assertThrows(IllegalStateException::class.java) {
            repo.save(Metric(null, "metric3"))
        }
    }
}