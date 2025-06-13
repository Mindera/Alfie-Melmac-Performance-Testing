import domain.OperativeSystem
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.sql.*

class OperSysRepoTest {

    private val connection = mockk<Connection>()
    private val statement = mockk<PreparedStatement>()
    private val resultSet = mockk<ResultSet>()

    @Test
    fun `findById returns operative system if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        every { resultSet.getInt("OperSysID") } returns 1
        every { resultSet.getString("OperSysName") } returns "Linux"

        val repo = repos.OperSysRepository(connection)
        val result = repo.findById(1)
        assertNotNull(result)
        assertEquals(1, result?.operSysId)
        assertEquals("Linux", result?.operSysName)
    }

    @Test
    fun `findById returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.OperSysRepository(connection)
        val result = repo.findById(1)
        assertNull(result)
    }

    @Test
    fun `findByName returns operative system if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setString(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        every { resultSet.getInt("OperSysID") } returns 2
        every { resultSet.getString("OperSysName") } returns "Windows"

        val repo = repos.OperSysRepository(connection)
        val result = repo.findByName("Windows")
        assertNotNull(result)
        assertEquals(2, result?.operSysId)
        assertEquals("Windows", result?.operSysName)
    }

    @Test
    fun `findByName returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setString(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.OperSysRepository(connection)
        val result = repo.findByName("NotFound")
        assertNull(result)
    }

    @Test
    fun `save inserts operative system and returns generated id`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(1, "MacOS") } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns true
        every { keys.getInt(1) } returns 99

        val repo = repos.OperSysRepository(connection)
        val id = repo.save(OperativeSystem(null, "MacOS"))
        assertEquals(99, id)
    }

    @Test
    fun `save throws if no generated key`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(1, "MacOS") } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns false

        val repo = repos.OperSysRepository(connection)
        assertThrows(IllegalStateException::class.java) {
            repo.save(OperativeSystem(null, "MacOS"))
        }
    }
}