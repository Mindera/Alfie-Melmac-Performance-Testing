package dtos

data class TestExecutionConfigDTO(
    val executionTypeName: String,
    val metricName: String,
    val metricParams: Map<String, String>,
    val executionTypeParams: Map<String, String>,
    val testThresholds: List<Triple<String, String, String>>?,
    val deviceName: String,
    val deviceSerialNumber: String? = null,
    val platform: String,
    val appName: String,
    val appVersion: String,
    val appPackage: String,
    val mainActivity: String? = null,
)