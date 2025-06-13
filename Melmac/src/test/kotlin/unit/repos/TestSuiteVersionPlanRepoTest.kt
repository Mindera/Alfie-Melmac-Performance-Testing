import domain.TestSuiteVersionPlan
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.sql.*

class TestSuiteVersionPlanRepoTest {

    private val connection = mockk<Connection>()
    private val statement = mockk<PreparedStatement>()
    private val resultSet = mockk<ResultSet>()

    @Test
    fun `findById returns plan if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 1) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        every { resultSet.getInt("TestSuiteVersionTestSuiteVersionID") } returns 1
        every { resultSet.getInt("TestPlanVersionTestPlanVersionID") } returns 2
        every { resultSet.getInt("Order") } returns 3

        val repo = repos.TestSuiteVersionPlanRepository(connection)
        val result = repo.findById(1)
        assertNotNull(result)
        assertEquals(1, result?.testSuiteVersionTestSuiteVersionId)
        assertEquals(2, result?.testPlanVersionTestPlanVersionId)
        assertEquals(3, result?.order)
    }

    @Test
    fun `findById returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 1) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.TestSuiteVersionPlanRepository(connection)
        val result = repo.findById(1)
        assertNull(result)
    }

    @Test
    fun `findByTestSuiteVersionId returns list of plans`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 5) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returnsMany listOf(true, true, false)
        every { resultSet.getInt("TestSuiteVersionTestSuiteVersionID") } returnsMany listOf(5, 5)
        every { resultSet.getInt("TestPlanVersionTestPlanVersionID") } returnsMany listOf(10, 11)
        every { resultSet.getInt("Order") } returnsMany listOf(1, 2)

        val repo = repos.TestSuiteVersionPlanRepository(connection)
        val result = repo.findByTestSuiteVersionId(5)
        assertEquals(2, result.size)
        assertEquals(10, result[0].testPlanVersionTestPlanVersionId)
        assertEquals(11, result[1].testPlanVersionTestPlanVersionId)
    }

    @Test
    fun `findByTestPlanVersionId returns list of plans`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 7) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returnsMany listOf(true, false)
        every { resultSet.getInt("TestSuiteVersionTestSuiteVersionID") } returns 8
        every { resultSet.getInt("TestPlanVersionTestPlanVersionID") } returns 7
        every { resultSet.getInt("Order") } returns 1

        val repo = repos.TestSuiteVersionPlanRepository(connection)
        val result = repo.findByTestPlanVersionId(7)
        assertEquals(1, result.size)
        assertEquals(8, result[0].testSuiteVersionTestSuiteVersionId)
        assertEquals(7, result[0].testPlanVersionTestPlanVersionId)
        assertEquals(1, result[0].order)
    }

    @Test
    fun `save inserts plan and returns generated id`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setInt(1, 5) } just Runs
        every { statement.setInt(2, 10) } just Runs
        every { statement.setInt(3, 1) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns true
        every { keys.getInt(1) } returns 99
        every { keys.close() } just Runs

        val repo = repos.TestSuiteVersionPlanRepository(connection)
        val id = repo.save(TestSuiteVersionPlan(5, 10, 1))
        assertEquals(99, id)
    }

    @Test
    fun `save throws if no generated key`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setInt(1, 5) } just Runs
        every { statement.setInt(2, 10) } just Runs
        every { statement.setInt(3, 1) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns false
        every { keys.close() } just Runs

        val repo = repos.TestSuiteVersionPlanRepository(connection)
        assertThrows(IllegalStateException::class.java) {
            repo.save(TestSuiteVersionPlan(5, 10, 1))
        }
    }
}