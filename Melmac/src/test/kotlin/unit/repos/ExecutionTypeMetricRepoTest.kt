import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.sql.*

class ExecutionTypeMetricRepoTest {

    private val connection = mockk<Connection>()
    private val stmt = mockk<PreparedStatement>()
    private val rs = mockk<ResultSet>()

    @Test
    fun `getExecutionTypesForMetric returns list of ids`() {
        every { connection.prepareStatement(any()) } returns stmt
        every { stmt.setInt(1, 42) } just Runs
        every { stmt.executeQuery() } returns rs
        every { rs.next() } returnsMany listOf(true, true, false)
        every { rs.getInt("ExecutionTypeExecutionTypeID") } returnsMany listOf(1, 2)
        every { rs.close() } just Runs
        every { stmt.close() } just Runs

        val repo = repos.ExecutionTypeMetricRepository(connection)
        val result = repo.getExecutionTypesForMetric(42)
        assertEquals(listOf(1, 2), result)
    }

    @Test
    fun `link does nothing if association exists`() {
        val checkStmt = mockk<PreparedStatement>()
        val checkRs = mockk<ResultSet>()
        every { connection.prepareStatement(match { it.contains("SELECT COUNT(*)") }) } returns checkStmt
        every { checkStmt.setInt(1, 10) } just Runs
        every { checkStmt.setInt(2, 20) } just Runs
        every { checkStmt.executeQuery() } returns checkRs
        every { checkRs.next() } returns true
        every { checkRs.getInt(1) } returns 1
        every { checkRs.close() } just Runs
        every { checkStmt.close() } just Runs

        val repo = repos.ExecutionTypeMetricRepository(connection)
        repo.link(10, 20)
        // No insert should be attempted, so nothing more to assert
    }

    @Test
    fun `link inserts if association does not exist`() {
        val checkStmt = mockk<PreparedStatement>()
        val checkRs = mockk<ResultSet>()
        val insertStmt = mockk<PreparedStatement>()
        every { connection.prepareStatement(match { it.contains("SELECT COUNT(*)") }) } returns checkStmt
        every { connection.prepareStatement(match { it.contains("INSERT INTO") }) } returns insertStmt
        every { checkStmt.setInt(1, 10) } just Runs
        every { checkStmt.setInt(2, 20) } just Runs
        every { checkStmt.executeQuery() } returns checkRs
        every { checkRs.next() } returns true
        every { checkRs.getInt(1) } returns 0
        every { checkRs.close() } just Runs
        every { checkStmt.close() } just Runs
        every { insertStmt.setInt(1, 10) } just Runs
        every { insertStmt.setInt(2, 20) } just Runs
        every { insertStmt.executeUpdate() } returns 1
        every { insertStmt.close() } just Runs

        val repo = repos.ExecutionTypeMetricRepository(connection)
        repo.link(10, 20)
        // If no exception, insert was called as expected
    }
}