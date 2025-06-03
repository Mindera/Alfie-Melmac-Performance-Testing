package domain

import java.time.LocalDateTime

data class TestPlanVersion(
    val testPlanVersionId: Int? = null,
    val version: String,
    val creationTimestamp: LocalDateTime,
    val notes: String?,
    val appPackage: String,
    val mainActivity: String? = null,
    val testPlanTestPlanId: Int,
    val deviceDeviceId: Int,
    val appVersionAppVersionId: Int,
    val executionTypeExecutionTypeId: Int,
)
