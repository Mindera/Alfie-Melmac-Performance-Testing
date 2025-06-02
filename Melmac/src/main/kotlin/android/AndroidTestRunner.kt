package android

import core.AppManager
import core.DeviceManager
import core.runners.ITestRunner
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import dtos.TestExecutionConfigDTO

/**
 * Class responsible for running Android tests.
 * Manages the lifecycle of the emulator, app installation, test execution, and cleanup.
 */
class AndroidTestRunner (
    private val deviceManager: DeviceManager,
    private val appManager: AppManager
) {S
    /**
     * Runs the specified test based on the provided configuration.
     *
     * @param config The configuration for the test execution.
     * @return A map containing the results of the test execution.
     */
    fun run(config: TestExecutionConfigDTO): Map<String, String> {
        deviceManager.startDevice(config.deviceName)
        appManager.installApp(config.appName)
        val result = when (config.metricName) {
            "App Startup Time" -> AndroidTestCommands.runAppStartupTest(config)
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