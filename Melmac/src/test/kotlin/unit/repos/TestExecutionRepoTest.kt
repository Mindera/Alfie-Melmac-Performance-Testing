import domain.TestExecution
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.sql.*
import java.time.LocalDateTime

class TestExecutionRepoTest {

    private val connection = mockk<Connection>()
    private val statement = mockk<PreparedStatement>()
    private val resultSet = mockk<ResultSet>()

    @Test
    fun `findAll returns list of test executions`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returnsMany listOf(true, true, false)
        every { resultSet.getInt("TestExecutionID") } returnsMany listOf(1, 2)
        val now = LocalDateTime.now()
        val later = now.plusMinutes(5)
        every { resultSet.getTimestamp("InitialTimestamp") } returnsMany listOf(Timestamp.valueOf(now), Timestamp.valueOf(now))
        every { resultSet.getTimestamp("EndTimestamp") } returnsMany listOf(Timestamp.valueOf(later), Timestamp.valueOf(later))
        every { resultSet.getString("Passed") } returnsMany listOf("true", "false")
        every { resultSet.getInt("TestPlanVersionTestPlanVersionID") } returnsMany listOf(10, 11)

        val repo = repos.TestExecutionRepository(connection)
        val result = repo.findAll()
        assertEquals(2, result.size)
        assertEquals(1, result[0].testExecutionId)
        assertEquals("true", result[0].passed)
        assertEquals(2, result[1].testExecutionId)
        assertEquals("false", result[1].passed)
    }

    @Test
    fun `findById returns test execution if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        every { resultSet.getInt("TestExecutionID") } returns 1
        val now = LocalDateTime.now()
        val later = now.plusMinutes(5)
        every { resultSet.getTimestamp("InitialTimestamp") } returns Timestamp.valueOf(now)
        every { resultSet.getTimestamp("EndTimestamp") } returns Timestamp.valueOf(later)
        every { resultSet.getString("Passed") } returns "true"
        every { resultSet.getInt("TestPlanVersionTestPlanVersionID") } returns 10

        val repo = repos.TestExecutionRepository(connection)
        val result = repo.findById(1)
        assertNotNull(result)
        assertEquals(1, result?.testExecutionId)
        assertEquals("true", result?.passed)
        assertEquals(10, result?.testPlanVersionTestPlanVersionId)
    }

    @Test
    fun `findById returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.TestExecutionRepository(connection)
        val result = repo.findById(1)
        assertNull(result)
    }

    @Test
    fun `save inserts test execution and returns generated id`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        val now = LocalDateTime.now()
        val later = now.plusMinutes(5)
        every { statement.setTimestamp(1, Timestamp.valueOf(now)) } just Runs
        every { statement.setTimestamp(2, Timestamp.valueOf(later)) } just Runs
        every { statement.setBoolean(3, true) } just Runs
        every { statement.setInt(4, 10) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns true
        every { keys.getInt(1) } returns 99

        val repo = repos.TestExecutionRepository(connection)
        val id = repo.save(TestExecution(null, now, later, "true", 10))
        assertEquals(99, id)
    }

    @Test
    fun `save throws if no generated key`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        val now = LocalDateTime.now()
        val later = now.plusMinutes(5)
        every { statement.setTimestamp(1, Timestamp.valueOf(now)) } just Runs
        every { statement.setTimestamp(2, Timestamp.valueOf(later)) } just Runs
        every { statement.setBoolean(3, true) } just Runs
        every { statement.setInt(4, 10) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns false

        val repo = repos.TestExecutionRepository(connection)
        assertThrows(IllegalStateException::class.java) {
            repo.save(TestExecution(null, now, later, "true", 10))
        }
    }
}