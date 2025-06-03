package mappers

import domain.TestExecution
import dtos.TestExecutionResponseDTO
import dtos.TestExecutionConfigDTO
import java.time.LocalDateTime

object TestExecutionMapper {
    fun toDto(testExecution: TestExecution): TestExecutionResponseDTO {
        return TestExecutionResponseDTO(
            testExecutionId = testExecution.testExecutionId ?: throw IllegalStateException("TestExecution ID cannot be null"),
            initialTimestamp = testExecution.initialTimestamp,
            endTimestamp = testExecution.endTimestamp,
            passed = testExecution.passed,
            testPlanVersionTestPlanVersionId = testExecution.testPlanVersionTestPlanVersionId
        )
    }

    fun toDomain(dto: TestExecutionResponseDTO): TestExecution {
        return TestExecution(
            testExecutionId = dto.testExecutionId,
            initialTimestamp = dto.initialTimestamp,
            endTimestamp = dto.endTimestamp,
            passed = dto.passed,
            testPlanVersionTestPlanVersionId = dto.testPlanVersionTestPlanVersionId
        )
    }

    /**
     * Builds a TestExecutionConfigDTO from all required parameters.
     * This mirrors the logic in your service.
     */
    fun toConfigDto(
        executionTypeName: String,
        metricName: String,
        metricParams: Map<String, String>,
        executionTypeParams: Map<String, String>,
        testThresholds: List<Triple<String, String, String>>?,
        deviceName: String,
        deviceSerialNumber: String?,
        platform: String,
        appName: String,
        appVersion: String,
        appPackage: String,
        mainActivity: String?
    ): TestExecutionConfigDTO {
        return TestExecutionConfigDTO(
            executionTypeName = executionTypeName,
            metricName = metricName,
            metricParams = metricParams,
            executionTypeParams = executionTypeParams,
            testThresholds = testThresholds,
            deviceName = deviceName,
            deviceSerialNumber = deviceSerialNumber,
            platform = platform,
            appName = appName,
            appVersion = appVersion,
            appPackage = appPackage,
            mainActivity = mainActivity
        )
    }
}