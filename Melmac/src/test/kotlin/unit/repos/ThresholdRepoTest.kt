import domain.TestThreshold
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.sql.*

class ThresholdRepoTest {

    private val connection = mockk<Connection>()
    private val statement = mockk<PreparedStatement>()
    private val resultSet = mockk<ResultSet>()

    @Test
    fun `findById returns threshold if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 1) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        every { resultSet.getInt("TestThresholdID") } returns 1
        every { resultSet.getInt("TargetValue") } returns 100
        every { resultSet.getInt("ThresholdTypeThresholdTypeID") } returns 2
        every { resultSet.getInt("TestPlanVersionTestPlanVersionID") } returns 3
        every { resultSet.getInt("MetricOutputMetricOutputID") } returns 4

        val repo = repos.ThresholdRepository(connection)
        val result = repo.findById(1)
        assertNotNull(result)
        assertEquals(1, result?.testThresholdId)
        assertEquals(100, result?.targetValue)
        assertEquals(2, result?.thresholdTypeThresholdTypeId)
        assertEquals(3, result?.testPlanVersionTestPlanVersionId)
        assertEquals(4, result?.metricOutputMetricOutputId)
    }

    @Test
    fun `findById returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 1) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.ThresholdRepository(connection)
        val result = repo.findById(1)
        assertNull(result)
    }

    @Test
    fun `findByTestPlanVersionId returns list of thresholds`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 3) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returnsMany listOf(true, true, false)
        every { resultSet.getInt("TestThresholdID") } returnsMany listOf(1, 2)
        every { resultSet.getInt("TargetValue") } returnsMany listOf(100, 200)
        every { resultSet.getInt("ThresholdTypeThresholdTypeID") } returnsMany listOf(2, 3)
        every { resultSet.getInt("TestPlanVersionTestPlanVersionID") } returnsMany listOf(3, 3)
        every { resultSet.getInt("MetricOutputMetricOutputID") } returnsMany listOf(4, 5)

        val repo = repos.ThresholdRepository(connection)
        val result = repo.findByTestPlanVersionId(3)
        assertEquals(2, result.size)
        assertEquals(1, result[0].testThresholdId)
        assertEquals(2, result[1].testThresholdId)
        assertEquals(100, result[0].targetValue)
        assertEquals(200, result[1].targetValue)
    }

    @Test
    fun `save inserts threshold and returns generated id`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setInt(1, 100) } just Runs
        every { statement.setInt(2, 2) } just Runs
        every { statement.setInt(3, 3) } just Runs
        every { statement.setInt(4, 4) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns true
        every { keys.getInt(1) } returns 99
        every { keys.close() } just Runs

        val repo = repos.ThresholdRepository(connection)
        val id = repo.save(TestThreshold(null, 100, 2, 3, 4))
        assertEquals(99, id)
    }

    @Test
    fun `save returns -1 if no generated key`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns false
        every { keys.close() } just Runs

        val repo = repos.ThresholdRepository(connection)
        val id = repo.save(TestThreshold(null, 100, 2, 3, 4))
        assertEquals(-1, id)
    }
}