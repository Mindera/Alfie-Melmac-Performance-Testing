import domain.App
import io.mockk.*
import mappers.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.sql.*

class AppRepoTest {

    private val connection = mockk<Connection>()
    private val statement = mockk<PreparedStatement>()
    private val resultSet = mockk<ResultSet>()

    @Test
    fun `findAll returns list of apps`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returnsMany listOf(true, true, false)
        every { resultSet.getInt("AppID") } returnsMany listOf(1, 2)
        every { resultSet.getString("AppName") } returnsMany listOf("App1", "App2")

        val repo = repos.AppRepository(connection)
        val result = repo.findAll()
        assertEquals(2, result.size)
        assertEquals("App1", result[0].appName)
        assertEquals("App2", result[1].appName)
    }

    @Test
    fun `findById returns app if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        every { resultSet.getInt("AppID") } returns 1
        every { resultSet.getString("AppName") } returns "App1"

        val repo = repos.AppRepository(connection)
        val result = repo.findById(1)
        assertNotNull(result)
        assertEquals(1, result?.appId)
        assertEquals("App1", result?.appName)
    }

    @Test
    fun `findById returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.AppRepository(connection)
        val result = repo.findById(1)
        assertNull(result)
    }

    @Test
    fun `findByName returns app if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setString(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        every { resultSet.getInt("AppID") } returns 2
        every { resultSet.getString("AppName") } returns "App2"

        val repo = repos.AppRepository(connection)
        val result = repo.findByName("App2")
        assertNotNull(result)
        assertEquals(2, result?.appId)
        assertEquals("App2", result?.appName)
    }

    @Test
    fun `findByName returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setString(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.AppRepository(connection)
        val result = repo.findByName("NotFound")
        assertNull(result)
    }

    @Test
    fun `save inserts app and returns generated id`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(any(), any()) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns true
        every { keys.getInt(1) } returns 42

        val repo = repos.AppRepository(connection)
        val id = repo.save(App(null, "NewApp"))
        assertEquals(42, id)
    }

    @Test
    fun `save throws if no generated key`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(any(), any()) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns false

        val repo = repos.AppRepository(connection)
        assertThrows(IllegalStateException::class.java) {
            repo.save(App(null, "NewApp"))
        }
    }
}