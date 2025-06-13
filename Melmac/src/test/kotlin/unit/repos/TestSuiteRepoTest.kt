import domain.TestSuite
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.sql.*
import java.time.LocalDateTime

class TestSuiteRepoTest {

    private val connection = mockk<Connection>()
    private val statement = mockk<PreparedStatement>()
    private val resultSet = mockk<ResultSet>()

    @Test
    fun `findAll returns list of test suites`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returnsMany listOf(true, true, false)
        every { resultSet.getInt("TestSuiteID") } returnsMany listOf(1, 2)
        every { resultSet.getString("TestSuiteName") } returnsMany listOf("SuiteA", "SuiteB")
        every { resultSet.getString("TestSuiteDescription") } returnsMany listOf("DescA", "DescB")
        val now = LocalDateTime.now()
        every { resultSet.getTimestamp("CreationTimestamp") } returnsMany listOf(Timestamp.valueOf(now), Timestamp.valueOf(now))
        every { resultSet.getString("IsActive") } returnsMany listOf("true", "false")

        val repo = repos.TestSuiteRepository(connection)
        val result = repo.findAll()
        assertEquals(2, result.size)
        assertEquals("SuiteA", result[0].testSuiteName)
        assertTrue(result[0].isActive)
        assertEquals("SuiteB", result[1].testSuiteName)
        assertFalse(result[1].isActive)
    }

    @Test
    fun `findById returns test suite if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        every { resultSet.getInt("TestSuiteID") } returns 1
        every { resultSet.getString("TestSuiteName") } returns "SuiteA"
        every { resultSet.getString("TestSuiteDescription") } returns "DescA"
        val now = LocalDateTime.now()
        every { resultSet.getTimestamp("CreationTimestamp") } returns Timestamp.valueOf(now)
        every { resultSet.getString("IsActive") } returns "true"

        val repo = repos.TestSuiteRepository(connection)
        val result = repo.findById(1)
        assertNotNull(result)
        assertEquals(1, result?.testSuiteId)
        assertEquals("SuiteA", result?.testSuiteName)
        assertEquals("DescA", result?.testSuiteDescription)
        assertEquals(now, result?.creationTimestamp)
        assertTrue(result?.isActive == true)
    }

    @Test
    fun `findById returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.TestSuiteRepository(connection)
        val result = repo.findById(1)
        assertNull(result)
    }

    @Test
    fun `save inserts test suite and returns generated id`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(1, "SuiteA") } just Runs
        every { statement.setString(2, "DescA") } just Runs
        val now = LocalDateTime.now()
        every { statement.setTimestamp(3, Timestamp.valueOf(now)) } just Runs
        every { statement.setString(4, "true") } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns true
        every { keys.getInt(1) } returns 99
        every { keys.close() } just Runs

        val repo = repos.TestSuiteRepository(connection)
        val id = repo.save(TestSuite(null, "SuiteA", "DescA", now, true))
        assertEquals(99, id)
    }

    @Test
    fun `save throws if no generated key`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(any(), any()) } just Runs
        every { statement.setTimestamp(any(), any()) } just Runs
        every { statement.setString(4, any()) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns false
        every { keys.close() } just Runs

        val repo = repos.TestSuiteRepository(connection)
        assertThrows(IllegalStateException::class.java) {
            repo.save(TestSuite(null, "SuiteA", "DescA", LocalDateTime.now(), true))
        }
    }

    @Test
    fun `findLatestVersionByTestSuiteId returns latest version if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 1) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        every { resultSet.getInt("LatestVersion") } returns 7

        val repo = repos.TestSuiteRepository(connection)
        val result = repo.findLatestVersionByTestSuiteId(1)
        assertEquals(7, result)
    }

    @Test
    fun `findLatestVersionByTestSuiteId returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 1) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.TestSuiteRepository(connection)
        val result = repo.findLatestVersionByTestSuiteId(1)
        assertNull(result)
    }
}