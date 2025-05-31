package repos

import domain.TestPlanVersion
import java.sql.Connection
import repos.IRepos.ITestPlanVersionRepository

class TestPlanVersionRepository(private val connection: Connection) : ITestPlanVersionRepository {

    override fun findById(id: Int): TestPlanVersion? {
        val query =
                """
            SELECT TestPlanVersionID, Version, CreationTimestamp, Notes, AppPackage, AppMainActivity, TestPlanTestPlanID, DeviceDeviceID, AppVersionAppVersionID, ExecutionTypeExecutionTypeID
            FROM TestPlanVersion WHERE TestPlanVersionID = ?
        """
        val statement = connection.prepareStatement(query)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            TestPlanVersion(
                    testPlanVersionId = resultSet.getInt("TestPlanVersionID"),
                    version = resultSet.getString("Version"),
                    creationTimestamp =
                            resultSet.getTimestamp("CreationTimestamp").toLocalDateTime(),
                    notes = resultSet.getString("Notes"),
                    appPackage = resultSet.getString("AppPackage"),
                    mainActivity = resultSet.getString("AppMainActivity"),
                    testPlanTestPlanId = resultSet.getInt("TestPlanTestPlanID"),
                    deviceDeviceId = resultSet.getInt("DeviceDeviceID"),
                    appVersionAppVersionId = resultSet.getInt("AppVersionAppVersionID"),
                    executionTypeExecutionTypeId = resultSet.getInt("ExecutionTypeExecutionTypeID"),
            )
        } else null
    }

    override fun save(testPlanVersion: TestPlanVersion): Int {
        val query =
                """
            INSERT INTO TestPlanVersion (Version, CreationTimestamp, Notes, AppPackage, AppMainActivity, TestPlanTestPlanID, DeviceDeviceID, AppVersionAppVersionID, ExecutionTypeExecutionTypeID)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """
        val statement = connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, testPlanVersion.version)
        statement.setTimestamp(2, java.sql.Timestamp.valueOf(testPlanVersion.creationTimestamp))
        statement.setString(3, testPlanVersion.notes)
        statement.setString(4, testPlanVersion.appPackage)
        statement.setString(5, testPlanVersion.mainActivity)
        statement.setInt(6, testPlanVersion.testPlanTestPlanId)
        statement.setInt(7, testPlanVersion.deviceDeviceId)
        statement.setInt(8, testPlanVersion.appVersionAppVersionId)
        statement.setInt(9, testPlanVersion.executionTypeExecutionTypeId)

        statement.executeUpdate()

        val resultSet = statement.generatedKeys
        return if (resultSet.next()) {
            resultSet.getInt(1)
        } else -1
    }

    override fun findLatestVersionByTestPlanId(testPlanId: Int): TestPlanVersion? {
        val query =
                """
            SELECT TOP 1 TestPlanVersionID, Version, CreationTimestamp, Notes, AppPackage, AppMainActivity, TestPlanTestPlanID, DeviceDeviceID, AppVersionAppVersionID, ExecutionTypeExecutionTypeID
            FROM TestPlanVersion
            WHERE TestPlanTestPlanID = ?
            ORDER BY CreationTimestamp DESC
            """
        val statement = connection.prepareStatement(query)
        statement.setInt(1, testPlanId)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            TestPlanVersion(
                    testPlanVersionId = resultSet.getInt("TestPlanVersionID"),
                    version = resultSet.getString("Version"),
                    creationTimestamp =
                            resultSet.getTimestamp("CreationTimestamp").toLocalDateTime(),
                    notes = resultSet.getString("Notes"),
                    appPackage = resultSet.getString("AppPackage"),
                    mainActivity = resultSet.getString("AppMainActivity"),
                    testPlanTestPlanId = resultSet.getInt("TestPlanTestPlanID"),
                    deviceDeviceId = resultSet.getInt("DeviceDeviceID"),
                    appVersionAppVersionId = resultSet.getInt("AppVersionAppVersionID"),
                    executionTypeExecutionTypeId = resultSet.getInt("ExecutionTypeExecutionTypeID"),
            )
        } else null
    }

    override fun findByTestPlanId(testPlanId: Int): List<TestPlanVersion> {
        val query =
                """
            SELECT TestPlanVersionID, Version, CreationTimestamp, Notes, AppPackage, AppMainActivity, TestPlanTestPlanID, DeviceDeviceID, AppVersionAppVersionID, ExecutionTypeExecutionTypeID
            FROM TestPlanVersion
            WHERE TestPlanTestPlanID = ?
            ORDER BY CreationTimestamp DESC
            """
        val statement = connection.prepareStatement(query)
        statement.setInt(1, testPlanId)
        val resultSet = statement.executeQuery()

        val versions = mutableListOf<TestPlanVersion>()
        while (resultSet.next()) {
            versions.add(
                    TestPlanVersion(
                            testPlanVersionId = resultSet.getInt("TestPlanVersionID"),
                            version = resultSet.getString("Version"),
                            creationTimestamp =
                                    resultSet.getTimestamp("CreationTimestamp").toLocalDateTime(),
                            notes = resultSet.getString("Notes"),
                            appPackage = resultSet.getString("AppPackage"),
                            mainActivity = resultSet.getString("AppMainActivity"),
                            testPlanTestPlanId = resultSet.getInt("TestPlanTestPlanID"),
                            deviceDeviceId = resultSet.getInt("DeviceDeviceID"),
                            appVersionAppVersionId = resultSet.getInt("AppVersionAppVersionID"),
                            executionTypeExecutionTypeId =
                                    resultSet.getInt("ExecutionTypeExecutionTypeID"),
                    )
            )
        }
        return versions
    }
}
