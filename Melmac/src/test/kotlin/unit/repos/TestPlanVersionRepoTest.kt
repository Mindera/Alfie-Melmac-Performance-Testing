import domain.TestPlanVersion
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.sql.*
import java.time.LocalDateTime

class TestPlanVersionRepoTest {

    private val connection = mockk<Connection>()
    private val statement = mockk<PreparedStatement>()
    private val resultSet = mockk<ResultSet>()

    private fun mockVersionResultSet(
        id: Int = 1,
        version: String = "v1",
        creation: LocalDateTime = LocalDateTime.now(),
        notes: String = "notes",
        appPackage: String = "pkg",
        mainActivity: String = "main",
        testPlanId: Int = 2,
        deviceId: Int = 3,
        appVersionId: Int = 4,
        execTypeId: Int = 5
    ) {
        every { resultSet.getInt("TestPlanVersionID") } returns id
        every { resultSet.getString("Version") } returns version
        every { resultSet.getTimestamp("CreationTimestamp") } returns Timestamp.valueOf(creation)
        every { resultSet.getString("Notes") } returns notes
        every { resultSet.getString("AppPackage") } returns appPackage
        every { resultSet.getString("AppMainActivity") } returns mainActivity
        every { resultSet.getInt("TestPlanTestPlanID") } returns testPlanId
        every { resultSet.getInt("DeviceDeviceID") } returns deviceId
        every { resultSet.getInt("AppVersionAppVersionID") } returns appVersionId
        every { resultSet.getInt("ExecutionTypeExecutionTypeID") } returns execTypeId
    }

    @Test
    fun `findById returns test plan version if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 1) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        mockVersionResultSet()

        val repo = repos.TestPlanVersionRepository(connection)
        val result = repo.findById(1)
        assertNotNull(result)
        assertEquals(1, result?.testPlanVersionId)
        assertEquals("v1", result?.version)
    }

    @Test
    fun `findById returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 1) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.TestPlanVersionRepository(connection)
        val result = repo.findById(1)
        assertNull(result)
    }

    @Test
    fun `save inserts test plan version and returns generated id`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(1, "v2") } just Runs
        every { statement.setTimestamp(2, any()) } just Runs
        every { statement.setString(3, "notes2") } just Runs
        every { statement.setString(4, "pkg2") } just Runs
        every { statement.setString(5, "main2") } just Runs
        every { statement.setInt(6, 10) } just Runs
        every { statement.setInt(7, 11) } just Runs
        every { statement.setInt(8, 12) } just Runs
        every { statement.setInt(9, 13) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns true
        every { keys.getInt(1) } returns 99
        every { keys.close() } just Runs

        val repo = repos.TestPlanVersionRepository(connection)
        val id = repo.save(
            TestPlanVersion(
                null, "v2", LocalDateTime.now(), "notes2", "pkg2", "main2",
                10, 11, 12, 13
            )
        )
        assertEquals(99, id)
    }

    @Test
    fun `save returns -1 if no generated key`() {
        val keys = mockk<ResultSet>()
        every { connection.prepareStatement(any(), any<Int>()) } returns statement
        every { statement.setString(any(), any()) } just Runs
        every { statement.setTimestamp(any(), any()) } just Runs
        every { statement.setInt(any(), any()) } just Runs
        every { statement.executeUpdate() } returns 1
        every { statement.generatedKeys } returns keys
        every { keys.next() } returns false

        val repo = repos.TestPlanVersionRepository(connection)
        val id = repo.save(
            TestPlanVersion(
                null, "v2", LocalDateTime.now(), "notes2", "pkg2", "main2",
                10, 11, 12, 13
            )
        )
        assertEquals(-1, id)
    }

    @Test
    fun `findLatestVersionByTestPlanId returns latest version if found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 2) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns true
        mockVersionResultSet(version = "latest", testPlanId = 2)

        val repo = repos.TestPlanVersionRepository(connection)
        val result = repo.findLatestVersionByTestPlanId(2)
        assertNotNull(result)
        assertEquals("latest", result?.version)
        assertEquals(2, result?.testPlanTestPlanId)
    }

    @Test
    fun `findLatestVersionByTestPlanId returns null if not found`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 2) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returns false

        val repo = repos.TestPlanVersionRepository(connection)
        val result = repo.findLatestVersionByTestPlanId(2)
        assertNull(result)
    }

    @Test
    fun `findByTestPlanId returns list of versions`() {
        every { connection.prepareStatement(any()) } returns statement
        every { statement.setInt(1, 2) } just Runs
        every { statement.executeQuery() } returns resultSet
        every { resultSet.next() } returnsMany listOf(true, true, false)
        mockVersionResultSet(id = 1, version = "v1", testPlanId = 2)
        mockVersionResultSet(id = 2, version = "v2", testPlanId = 2)

        // To simulate two different rows, we need to update the mock for each call
        var callCount = 0
        every { resultSet.getInt("TestPlanVersionID") } answers { if (callCount++ == 0) 1 else 2 }
        every { resultSet.getString("Version") } answers { if (callCount == 1) "v1" else "v2" }

        val repo = repos.TestPlanVersionRepository(connection)
        val result = repo.findByTestPlanId(2)
        assertEquals(2, result.size)
        assertEquals("v1", result[0].version)
        assertEquals("v2", result[1].version)
    }
}