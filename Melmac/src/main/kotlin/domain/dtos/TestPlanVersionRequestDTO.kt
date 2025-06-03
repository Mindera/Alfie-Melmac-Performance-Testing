package dtos
import dtos.TestThresholdRequestDTO
import dtos.TestMetricParameterRequestDTO

data class TestPlanVersionRequestDTO(
    val notes: String?,
    val testName: String,
    val metricMetricId: Int,
    val deviceName: String,
    val appName: String,
    val appVersion: String,
    val appPackage: String,
    val mainActivity: String? = null,
    val executionTypeExecutionTypeId: Int,
    val thresholds: List<TestThresholdRequestDTO>,
    val metricParameters: List<TestMetricParameterRequestDTO>,
    val executionTypeParameters: List<TestExecutionTypeParameterRequestDTO>,
    val testSuiteVersionId: Int,
)

