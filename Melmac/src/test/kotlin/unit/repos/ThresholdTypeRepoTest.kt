import domain.ThresholdType
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.sql.*

class ThresholdTypeRepoTest {

    private val connection = mockk<Connection>()
    private val statement = mockk<PreparedStatement>()
    private val resultSet = mockk<ResultSet>()

    @Test
    fun `findById returns threshold type if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 1) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        every { resultSet.getInt("ThresholdTypeID") } returns 1
        every { resultSet.getString("ThresholdTypeName") } returns "Upper"
        every { resultSet.getString("ThresholdTypeDescription") } returns "Upper bound"

        val repo = repos.ThresholdTypeRepository(connection)
        val result = repo.findById(1)
        assertNotNull(result)
        assertEquals(1, result?.thresholdTypeId)
        assertEquals("Upper", result?.thresholdTypeName)
        assertEquals("Upper bound", result?.thresholdTypeDescription)
    }

    @Test
    fun `findById returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 1) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.ThresholdTypeRepository(connection)
        val result = repo.findById(1)
        assertNull(result)
    }

    @Test
    fun `findAll returns list of threshold types`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returnsMany listOf(true, true, false)
        every { resultSet.getInt("ThresholdTypeID") } returnsMany listOf(1, 2)
        every { resultSet.getString("ThresholdTypeName") } returnsMany listOf("Upper", "Lower")
        every { resultSet.getString("ThresholdTypeDescription") } returnsMany listOf("Upper bound", "Lower bound")

        val repo = repos.ThresholdTypeRepository(connection)
        val result = repo.findAll()
        assertEquals(2, result.size)
        assertEquals("Upper", result[0].thresholdTypeName)
        assertEquals("Lower", result[1].thresholdTypeName)
    }

    @Test
    fun `findByName returns threshold type if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setString(1, "Upper") } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        every { resultSet.getInt("ThresholdTypeID") } returns 1
        every { resultSet.getString("ThresholdTypeName") } returns "Upper"
        every { resultSet.getString("ThresholdTypeDescription") } returns "Upper bound"

        val repo = repos.ThresholdTypeRepository(connection)
        val result = repo.findByName("Upper")
        assertNotNull(result)
        assertEquals(1, result?.thresholdTypeId)
        assertEquals("Upper", result?.thresholdTypeName)
        assertEquals("Upper bound", result?.thresholdTypeDescription)
    }

    @Test
    fun `findByName returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setString(1, "Upper") } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.ThresholdTypeRepository(connection)
        val result = repo.findByName("Upper")
        assertNull(result)
    }

    @Test
    fun `save inserts threshold type and returns generated id`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(1, "Upper") } just Runs
        every { statement.setString(2, "Upper bound") } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns true
        every { keys.getInt(1) } returns 99
        every { keys.close() } just Runs

        val repo = repos.ThresholdTypeRepository(connection)
        val id = repo.save(ThresholdType(null, "Upper", "Upper bound"))
        assertEquals(99, id)
    }

    @Test
    fun `save throws if no generated key`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(1, "Upper") } just Runs
        every { statement.setString(2, "Upper bound") } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns false
        every { keys.close() } just Runs

        val repo = repos.ThresholdTypeRepository(connection)
        assertThrows(IllegalStateException::class.java) {
            repo.save(ThresholdType(null, "Upper", "Upper bound"))
        }
    }

    @Test
    fun `update updates threshold type and returns affected rows`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setString(1, "Upper") } just Runs
        every { statement.setString(2, "Upper bound") } just Runs
        every { statement.setInt(3, 1) } just Runs
        every { statement.executeUpdate() } returns 1

        val repo = repos.ThresholdTypeRepository(connection)
        val affected = repo.update(ThresholdType(1, "Upper", "Upper bound"))
        assertEquals(1, affected)
    }
}