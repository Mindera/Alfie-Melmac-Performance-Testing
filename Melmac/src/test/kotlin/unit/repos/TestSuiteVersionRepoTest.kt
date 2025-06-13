import domain.TestSuiteVersion
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.sql.*
import java.time.LocalDateTime

class TestSuiteVersionRepoTest {

    private val connection = mockk<Connection>()
    private val statement = mockk<PreparedStatement>()
    private val resultSet = mockk<ResultSet>()

    @Test
    fun `findLatestVersionByTestSuiteId returns latest version if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 1) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        every { resultSet.getInt("TestSuiteVersionID") } returns 10
        every { resultSet.getString("Version") } returns "3"
        val now = LocalDateTime.now()
        every { resultSet.getTimestamp("CreationTimestamp") } returns Timestamp.valueOf(now)
        every { resultSet.getString("Notes") } returns "note"
        every { resultSet.getInt("TestSuiteTestSuiteID") } returns 1

        val repo = repos.TestSuiteVersionRepository(connection)
        val result = repo.findLatestVersionByTestSuiteId(1)
        assertNotNull(result)
        assertEquals(10, result?.testSuiteVersionId)
        assertEquals("3", result?.version)
        assertEquals(now, result?.creationTimestamp)
        assertEquals("note", result?.notes)
        assertEquals(1, result?.testSuiteTestSuiteId)
    }

    @Test
    fun `findLatestVersionByTestSuiteId returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 1) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.TestSuiteVersionRepository(connection)
        val result = repo.findLatestVersionByTestSuiteId(1)
        assertNull(result)
    }

    @Test
    fun `save inserts test suite version and returns generated id`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(1, "4") } just Runs
        val now = LocalDateTime.now()
        every { statement.setTimestamp(2, Timestamp.valueOf(now)) } just Runs
        every { statement.setString(3, "note2") } just Runs
        every { statement.setInt(4, 2) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns true
        every { keys.getInt(1) } returns 99
        every { keys.close() } just Runs

        val repo = repos.TestSuiteVersionRepository(connection)
        val id = repo.save(TestSuiteVersion(null, "4", now, "note2", 2))
        assertEquals(99, id)
    }

    @Test
    fun `save throws if no generated key`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(any(), any()) } just Runs
        every { statement.setTimestamp(any(), any()) } just Runs
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns false
        every { keys.close() } just Runs

        val repo = repos.TestSuiteVersionRepository(connection)
        assertThrows(IllegalStateException::class.java) {
            repo.save(TestSuiteVersion(null, "4", LocalDateTime.now(), "note2", 2))
        }
    }
}