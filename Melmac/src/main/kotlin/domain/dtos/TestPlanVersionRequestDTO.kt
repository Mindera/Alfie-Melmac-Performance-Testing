package dtos
import dtos.TestThresholdRequestDTO
import dtos.TestMetricParameterRequestDTO

data class TestPlanVersionRequestDTO(
    val notes: String?,
    val testName: String,
    val metricName: String,
    val deviceName: String,
    val appName: String,
    val appVersion: String,
    val appPackage: String,
    val mainActivity: String? = null,
    val executionType: String,
    val thresholds: List<TestThresholdRequestDTO>,
    val metricParameters: List<TestMetricParameterRequestDTO>,
    val executionTypeParameters: List<TestExecutionTypeParameterRequestDTO>,
    val testSuiteVersionId: Int,
)

