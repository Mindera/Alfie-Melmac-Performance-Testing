import domain.OSVersion
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.sql.*

class OperSysVersionRepoTest {

    private val connection = mockk<Connection>()
    private val statement = mockk<PreparedStatement>()
    private val resultSet = mockk<ResultSet>()

    @Test
    fun `findById returns OSVersion if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        every { resultSet.getInt("OSVersionID") } returns 1
        every { resultSet.getString("Version") } returns "11"
        every { resultSet.getInt("OperativeSystemOperSysID") } returns 2

        val repo = repos.OperSysVersionRepository(connection)
        val result = repo.findById(1)
        assertNotNull(result)
        assertEquals(1, result?.osVersionId)
        assertEquals("11", result?.version)
        assertEquals(2, result?.operativeSystemOperSysId)
    }

    @Test
    fun `findById returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.OperSysVersionRepository(connection)
        val result = repo.findById(1)
        assertNull(result)
    }

    @Test
    fun `findByOperSysId returns list of OSVersions`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returnsMany listOf(true, true, false)
        every { resultSet.getInt("OSVersionID") } returnsMany listOf(1, 2)
        every { resultSet.getString("Version") } returnsMany listOf("11", "12")
        every { resultSet.getInt("OperativeSystemOperSysID") } returnsMany listOf(2, 2)

        val repo = repos.OperSysVersionRepository(connection)
        val result = repo.findByOperSysId(2)
        assertEquals(2, result.size)
        assertEquals("11", result[0].version)
        assertEquals("12", result[1].version)
    }

    @Test
    fun `save inserts OSVersion and returns generated id`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(1, "13") } just Runs
        every { statement.setInt(2, 3) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns true
        every { keys.getInt(1) } returns 99

        val repo = repos.OperSysVersionRepository(connection)
        val id = repo.save(OSVersion(null, "13", 3))
        assertEquals(99, id)
    }

    @Test
    fun `save throws if no generated key`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(1, "13") } just Runs
        every { statement.setInt(2, 3) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns false

        val repo = repos.OperSysVersionRepository(connection)
        assertThrows(IllegalStateException::class.java) {
            repo.save(OSVersion(null, "13", 3))
        }
    }
}