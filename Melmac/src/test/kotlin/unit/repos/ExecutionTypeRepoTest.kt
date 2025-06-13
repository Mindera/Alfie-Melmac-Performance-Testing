import domain.ExecutionType
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.sql.*

class ExecutionTypeRepoTest {

    private val connection = mockk<Connection>()
    private val statement = mockk<PreparedStatement>()
    private val resultSet = mockk<ResultSet>()

    @Test
    fun `findById returns execution type if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        every { resultSet.getInt("ExecutionTypeID") } returns 1
        every { resultSet.getString("ExecutionTypeName") } returns "TypeA"
        every { resultSet.getString("ExecutionTypeDescription") } returns "desc"

        val repo = repos.ExecutionTypeRepository(connection)
        val result = repo.findById(1)
        assertNotNull(result)
        assertEquals(1, result?.executionTypeId)
        assertEquals("TypeA", result?.executionTypeName)
        assertEquals("desc", result?.executionTypeDescription)
    }

    @Test
    fun `findById returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.ExecutionTypeRepository(connection)
        val result = repo.findById(1)
        assertNull(result)
    }

    @Test
    fun `findByMetricId returns list of execution types`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returnsMany listOf(true, true, false)
        every { resultSet.getInt("ExecutionTypeID") } returnsMany listOf(1, 2)
        every { resultSet.getString("ExecutionTypeName") } returnsMany listOf("TypeA", "TypeB")
        every { resultSet.getString("ExecutionTypeDescription") } returnsMany listOf("descA", "descB")

        val repo = repos.ExecutionTypeRepository(connection)
        val result = repo.findByMetricId(5)
        assertEquals(2, result.size)
        assertEquals("TypeA", result[0].executionTypeName)
        assertEquals("TypeB", result[1].executionTypeName)
    }

    @Test
    fun `findByMetricIdAndName returns execution type if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 5) } just Runs
        every { statement.setString(2, "TypeA") } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        every { resultSet.getInt("ExecutionTypeID") } returns 1
        every { resultSet.getString("ExecutionTypeName") } returns "TypeA"
        every { resultSet.getString("ExecutionTypeDescription") } returns "descA"

        val repo = repos.ExecutionTypeRepository(connection)
        val result = repo.findByMetricIdAndName(5, "TypeA")
        assertNotNull(result)
        assertEquals(1, result?.executionTypeId)
        assertEquals("TypeA", result?.executionTypeName)
        assertEquals("descA", result?.executionTypeDescription)
    }

    @Test
    fun `findByMetricIdAndName returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 5) } just Runs
        every { statement.setString(2, "TypeA") } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.ExecutionTypeRepository(connection)
        val result = repo.findByMetricIdAndName(5, "TypeA")
        assertNull(result)
    }

    @Test
    fun `findByName returns execution type if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setString(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        every { resultSet.getInt("ExecutionTypeID") } returns 2
        every { resultSet.getString("ExecutionTypeName") } returns "TypeB"
        every { resultSet.getString("ExecutionTypeDescription") } returns "descB"

        val repo = repos.ExecutionTypeRepository(connection)
        val result = repo.findByName("TypeB")
        assertNotNull(result)
        assertEquals(2, result?.executionTypeId)
        assertEquals("TypeB", result?.executionTypeName)
        assertEquals("descB", result?.executionTypeDescription)
    }

    @Test
    fun `findByName returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setString(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.ExecutionTypeRepository(connection)
        val result = repo.findByName("TypeB")
        assertNull(result)
    }

    @Test
    fun `save inserts execution type and returns generated id`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(1, "TypeC") } just Runs
        every { statement.setString(2, "descC") } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns true
        every { keys.getInt(1) } returns 99

        val repo = repos.ExecutionTypeRepository(connection)
        val id = repo.save(ExecutionType(null, "TypeC", "descC"))
        assertEquals(99, id)
    }

    @Test
    fun `save throws if no generated key`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(1, "TypeC") } just Runs
        every { statement.setString(2, "descC") } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns false

        val repo = repos.ExecutionTypeRepository(connection)
        assertThrows(IllegalStateException::class.java) {
            repo.save(ExecutionType(null, "TypeC", "descC"))
        }
    }

    @Test
    fun `update updates execution type description`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setString(1, "descD") } just Runs
        every { statement.setInt(2, 5) } just Runs
        every { statement.executeUpdate() } returns 1

        val repo = repos.ExecutionTypeRepository(connection)
        repo.update(ExecutionType(5, "TypeD", "descD"))
        // No exception means success
    }
}