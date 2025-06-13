import domain.ExecutionTypeParameter
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.sql.*

class ExecutionTypeParameterRepoTest {

    private val connection = mockk<Connection>()
    private val statement = mockk<PreparedStatement>()
    private val resultSet = mockk<ResultSet>()

    @Test
    fun `findById returns parameter if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        every { resultSet.getInt("ExecutionTypeParameterID") } returns 1
        every { resultSet.getString("ParameterName") } returns "param"
        every { resultSet.getString("ParameterType") } returns "String"
        every { resultSet.getInt("ExecutionTypeExecutionTypeID") } returns 10

        val repo = repos.ExecutionTypeParameterRepository(connection)
        val result = repo.findById(1)
        assertNotNull(result)
        assertEquals(1, result?.executionTypeParameterId)
        assertEquals("param", result?.parameterName)
        assertEquals("String", result?.parameterType)
        assertEquals(10, result?.executionTypeExecutionTypeId)
    }

    @Test
    fun `findById returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.ExecutionTypeParameterRepository(connection)
        val result = repo.findById(1)
        assertNull(result)
    }

    @Test
    fun `findByExecutionTypeId returns list of parameters`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returnsMany listOf(true, true, false)
        every { resultSet.getInt("ExecutionTypeParameterID") } returnsMany listOf(1, 2)
        every { resultSet.getString("ParameterName") } returnsMany listOf("param1", "param2")
        every { resultSet.getString("ParameterType") } returnsMany listOf("String", "Int")
        every { resultSet.getInt("ExecutionTypeExecutionTypeID") } returnsMany listOf(10, 10)

        val repo = repos.ExecutionTypeParameterRepository(connection)
        val result = repo.findByExecutionTypeId(10)
        assertEquals(2, result.size)
        assertEquals("param1", result[0].parameterName)
        assertEquals("param2", result[1].parameterName)
    }

    @Test
    fun `findByExecutionTypeIdAndName returns parameter if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 10) } just Runs
        every { statement.setString(2, "param") } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        every { resultSet.getInt("ExecutionTypeParameterID") } returns 3
        every { resultSet.getString("ParameterName") } returns "param"
        every { resultSet.getString("ParameterType") } returns "Bool"
        every { resultSet.getInt("ExecutionTypeExecutionTypeID") } returns 10

        val repo = repos.ExecutionTypeParameterRepository(connection)
        val result = repo.findByExecutionTypeIdAndName(10, "param")
        assertNotNull(result)
        assertEquals(3, result?.executionTypeParameterId)
        assertEquals("param", result?.parameterName)
        assertEquals("Bool", result?.parameterType)
        assertEquals(10, result?.executionTypeExecutionTypeId)
    }

    @Test
    fun `findByExecutionTypeIdAndName returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 10) } just Runs
        every { statement.setString(2, "param") } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.ExecutionTypeParameterRepository(connection)
        val result = repo.findByExecutionTypeIdAndName(10, "param")
        assertNull(result)
    }

    @Test
    fun `save inserts parameter and returns generated id`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(1, "param") } just Runs
        every { statement.setString(2, "String") } just Runs
        every { statement.setInt(3, 10) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns true
        every { keys.getInt(1) } returns 99

        val repo = repos.ExecutionTypeParameterRepository(connection)
        val id = repo.save(ExecutionTypeParameter(null, "param", "String", 10))
        assertEquals(99, id)
    }

    @Test
    fun `save throws if no generated key`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(1, "param") } just Runs
        every { statement.setString(2, "String") } just Runs
        every { statement.setInt(3, 10) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns false

        val repo = repos.ExecutionTypeParameterRepository(connection)
        assertThrows(IllegalStateException::class.java) {
            repo.save(ExecutionTypeParameter(null, "param", "String", 10))
        }
    }

    @Test
    fun `update updates parameter type`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setString(1, "Int") } just Runs
        every { statement.setInt(2, 5) } just Runs
        every { statement.executeUpdate() } returns 1

        val repo = repos.ExecutionTypeParameterRepository(connection)
        repo.update(ExecutionTypeParameter(5, "param", "Int", 10))
        // No exception means success
    }
}