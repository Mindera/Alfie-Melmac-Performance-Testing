package ios

import core.AppManager
import core.DeviceManager
import dtos.TestExecutionConfigDTO

/**
 * Class responsible for running iOS tests. Delegates test execution to XCUITestCommands.
 */
class IOSTestRunner(
    private val deviceManager: DeviceManager,
    private val appManager: AppManager
) {
    fun run(config: TestExecutionConfigDTO): Map<String, String> {
        deviceManager.startDevice(config.deviceName)
        appManager.installApp(config.appName)
        val result = when (config.metricName) {
            "App Startup Time", "launchDuration" -> XCUITestCommands.runAppStartupTest(config)
            else -> {
                println("⚠️ Unsupported metric: ${config.metricName}")
                emptyMap()
            }
        }
        appManager.uninstallApp(config.appPackage)
        deviceManager.shutdownDevice(config.deviceName)
        return result
    }
}