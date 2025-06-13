import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.sql.*
import java.time.Instant

class BootstrapUpdateRepoTest {

    private val connection = mockk<Connection>()
    private val statement = mockk<PreparedStatement>(relaxed = true)
    private val resultSet = mockk<ResultSet>(relaxed = true)

    @Test
    fun `getLatestUpdateDate returns Instant if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        val now = Instant.now()
        val timestamp = Timestamp.from(now)
        every { resultSet.getTimestamp("UpdateDate") } returns timestamp

        val repo = repos.BootstrapUpdateRepository(connection)
        val result = repo.getLatestUpdateDate()
        assertEquals(now, result)
    }

    @Test
    fun `getLatestUpdateDate returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.BootstrapUpdateRepository(connection)
        val result = repo.getLatestUpdateDate()
        assertNull(result)
    }

    @Test
    fun `save returns true if insert successful`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setTimestamp(any(), any()) } just Runs
        every { statement.executeUpdate() } returns 1

        val repo = repos.BootstrapUpdateRepository(connection)
        val result = repo.save(Instant.now())
        assertTrue(result)
    }

    @Test
    fun `save returns false if insert not successful`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setTimestamp(any(), any()) } just Runs
        every { statement.executeUpdate() } returns 0

        val repo = repos.BootstrapUpdateRepository(connection)
        val result = repo.save(Instant.now())
        assertFalse(result)
    }
}