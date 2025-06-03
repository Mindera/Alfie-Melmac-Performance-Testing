package mappers

import domain.TestPlanVersion
import dtos.*
import java.time.LocalDateTime

object TestPlanVersionMapper {
    fun toDto(
        testPlanVersion: TestPlanVersion,
        thresholds: List<TestThresholdResponseDTO>,
        metricParameters: List<TestMetricParameterResponseDTO>,
        executionTypeParameters: List<TestExecutionTypeParameterResponseDTO>,
        testSuiteVersionId: Int
    ): TestPlanVersionResponseDTO {
        return TestPlanVersionResponseDTO(
            testPlanVersionId = testPlanVersion.testPlanVersionId ?: throw IllegalStateException("TestPlanVersion ID cannot be null"),
            version = testPlanVersion.version,
            creationTimestamp = testPlanVersion.creationTimestamp,
            notes = testPlanVersion.notes,
            testPlanTestPlanId = testPlanVersion.testPlanTestPlanId,
            deviceDeviceId = testPlanVersion.deviceDeviceId,
            appVersionAppVersionId = testPlanVersion.appVersionAppVersionId,
            appPackage = testPlanVersion.appPackage,
            mainActivity = testPlanVersion.mainActivity,
            executionTypeExecutionTypeId = testPlanVersion.executionTypeExecutionTypeId,
            thresholds = thresholds,
            metricParameters = metricParameters,
            executionTypeParameters = executionTypeParameters,
            testSuiteVersionId = testSuiteVersionId
        )
    }

    fun toDomain(dto: TestPlanVersionResponseDTO): TestPlanVersion {
        return TestPlanVersion(
            testPlanVersionId = dto.testPlanVersionId,
            version = dto.version,
            creationTimestamp = dto.creationTimestamp,
            notes = dto.notes,
            appPackage = dto.appPackage,
            mainActivity = dto.mainActivity,
            testPlanTestPlanId = dto.testPlanTestPlanId,
            deviceDeviceId = dto.deviceDeviceId,
            appVersionAppVersionId = dto.appVersionAppVersionId,
            executionTypeExecutionTypeId = dto.executionTypeExecutionTypeId
        )
    }
}