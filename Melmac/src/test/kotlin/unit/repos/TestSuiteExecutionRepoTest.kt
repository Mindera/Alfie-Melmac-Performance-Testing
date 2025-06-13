import domain.SuiteExecution
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.sql.*
import java.time.LocalDateTime

class TestSuiteExecutionRepoTest {

    private val connection = mockk<Connection>()
    private val statement = mockk<PreparedStatement>()
    private val resultSet = mockk<ResultSet>()

    @Test
    fun `findById returns suite execution if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        every { resultSet.getInt("SuiteExecutionID") } returns 1
        val now = LocalDateTime.now()
        val later = now.plusMinutes(10)
        every { resultSet.getTimestamp("InitialTimestamp") } returns Timestamp.valueOf(now)
        every { resultSet.getTimestamp("EndTimestamp") } returns Timestamp.valueOf(later)
        every { resultSet.getInt("TestSuiteVersionTestSuiteVersionID") } returns 5

        val repo = repos.TestSuiteExecutionRepository(connection)
        val result = repo.findById(1)
        assertNotNull(result)
        assertEquals(1, result?.suiteExecutionId)
        assertEquals(now, result?.initialTimestamp)
        assertEquals(later, result?.endTimestamp)
        assertEquals(5, result?.testSuiteVersionTestSuiteVersionId)
    }

    @Test
    fun `findById returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.TestSuiteExecutionRepository(connection)
        val result = repo.findById(1)
        assertNull(result)
    }

    @Test
    fun `findByTestSuiteVersionId returns list of suite executions`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returnsMany listOf(true, true, false)
        every { resultSet.getInt("SuiteExecutionID") } returnsMany listOf(1, 2)
        val now = LocalDateTime.now()
        val later = now.plusMinutes(10)
        every { resultSet.getTimestamp("InitialTimestamp") } returnsMany listOf(Timestamp.valueOf(now), Timestamp.valueOf(now))
        every { resultSet.getTimestamp("EndTimestamp") } returnsMany listOf(Timestamp.valueOf(later), Timestamp.valueOf(later))
        every { resultSet.getInt("TestSuiteVersionTestSuiteVersionID") } returnsMany listOf(5, 5)

        val repo = repos.TestSuiteExecutionRepository(connection)
        val result = repo.findByTestSuiteVersionId(5)
        assertEquals(2, result.size)
        assertEquals(1, result[0].suiteExecutionId)
        assertEquals(2, result[1].suiteExecutionId)
    }

    @Test
    fun `save inserts suite execution and returns generated id`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        val now = LocalDateTime.now()
        val later = now.plusMinutes(10)
        every { statement.setTimestamp(1, Timestamp.valueOf(now)) } just Runs
        every { statement.setTimestamp(2, Timestamp.valueOf(later)) } just Runs
        every { statement.setInt(3, 5) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns true
        every { keys.getInt(1) } returns 99

        val repo = repos.TestSuiteExecutionRepository(connection)
        val id = repo.save(SuiteExecution(null, now, later, 5))
        assertEquals(99, id)
    }

    @Test
    fun `save throws if no generated key`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        val now = LocalDateTime.now()
        val later = now.plusMinutes(10)
        every { statement.setTimestamp(1, Timestamp.valueOf(now)) } just Runs
        every { statement.setTimestamp(2, Timestamp.valueOf(later)) } just Runs
        every { statement.setInt(3, 5) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns false

        val repo = repos.TestSuiteExecutionRepository(connection)
        assertThrows(IllegalStateException::class.java) {
            repo.save(SuiteExecution(null, now, later, 5))
        }
    }
}