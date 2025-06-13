import domain.AppVersion
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.sql.*

class AppVersionRepoTest {

    private val connection = mockk<Connection>()
    private val statement = mockk<PreparedStatement>()
    private val resultSet = mockk<ResultSet>()

    @Test
    fun `findByAppId returns list of app versions`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returnsMany listOf(true, true, false)
        every { resultSet.getInt("AppVersionID") } returnsMany listOf(1, 2)
        every { resultSet.getInt("AppAppID") } returnsMany listOf(10, 10)
        every { resultSet.getString("Version") } returnsMany listOf("1.0", "2.0")

        val repo = repos.AppVersionRepository(connection)
        val result = repo.findByAppId(10)
        assertEquals(2, result.size)
        assertEquals("1.0", result[0].appVersion)
        assertEquals("2.0", result[1].appVersion)
    }

    @Test
    fun `findById returns app version if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        every { resultSet.getInt("AppVersionID") } returns 1
        every { resultSet.getInt("AppAppID") } returns 10
        every { resultSet.getString("Version") } returns "1.0"

        val repo = repos.AppVersionRepository(connection)
        val result = repo.findById(1)
        assertNotNull(result)
        assertEquals(1, result?.appVersionId)
        assertEquals(10, result?.appId)
        assertEquals("1.0", result?.appVersion)
    }

    @Test
    fun `findById returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.AppVersionRepository(connection)
        val result = repo.findById(1)
        assertNull(result)
    }

    @Test
    fun `findByName returns app version if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setString(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        every { resultSet.getInt("AppVersionID") } returns 2
        every { resultSet.getInt("AppAppID") } returns 11
        every { resultSet.getString("Version") } returns "2.0"

        val repo = repos.AppVersionRepository(connection)
        val result = repo.findByName("2.0")
        assertNotNull(result)
        assertEquals(2, result?.appVersionId)
        assertEquals(11, result?.appId)
        assertEquals("2.0", result?.appVersion)
    }

    @Test
    fun `findByName returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setString(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.AppVersionRepository(connection)
        val result = repo.findByName("notfound")
        assertNull(result)
    }

    @Test
    fun `findByAppIdAndVersion returns app version if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 10) } just Runs
        every { statement.setString(2, "1.0") } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        every { resultSet.getInt("AppVersionID") } returns 3
        every { resultSet.getInt("AppAppID") } returns 10
        every { resultSet.getString("Version") } returns "1.0"

        val repo = repos.AppVersionRepository(connection)
        val result = repo.findByAppIdAndVersion(10, "1.0")
        assertNotNull(result)
        assertEquals(3, result?.appVersionId)
        assertEquals(10, result?.appId)
        assertEquals("1.0", result?.appVersion)
    }

    @Test
    fun `findByAppIdAndVersion returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 10) } just Runs
        every { statement.setString(2, "notfound") } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.AppVersionRepository(connection)
        val result = repo.findByAppIdAndVersion(10, "notfound")
        assertNull(result)
    }

    @Test
    fun `save inserts app version and returns generated id`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(1, "3.0") } just Runs
        every { statement.setInt(2, 12) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns true
        every { keys.getInt(1) } returns 99

        val repo = repos.AppVersionRepository(connection)
        val id = repo.save(AppVersion(null, 12, "3.0"))
        assertEquals(99, id)
    }

    @Test
    fun `save throws if no generated key`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(1, "3.0") } just Runs
        every { statement.setInt(2, 12) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns false

        val repo = repos.AppVersionRepository(connection)
        assertThrows(IllegalStateException::class.java) {
            repo.save(AppVersion(null, 12, "3.0"))
        }
    }
}