package android

import com.fasterxml.jackson.databind.JsonNode
import core.AppManager
import core.DeviceManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import utils.Logger

/**
 * Class responsible for running Android tests.
 * It manages the lifecycle of the emulator, app installation, test execution, and cleanup.
 */
class AndroidTestRunner : KoinComponent {

    // Dependency injection for managing Android devices
    private val deviceManager: DeviceManager by inject(named("android"))

    // Dependency injection for managing Android apps
    private val appManager: AppManager by inject(named("android"))

    /**
     * Executes the Android test based on the provided configuration.
     *
     * @param config A JSON node containing the test configuration.
     * - `device_name`: The name of the Android emulator/device to use.
     * - `apk_path`: The file path to the APK to be installed.
     * - `package_name`: The package name of the app to be tested.
     * - `main_activity`: The main activity of the app to be launched.
     * - `target_resource_id`: The resource ID of the target UI element to locate.
     */
    fun run(config: JsonNode) {
        val deviceName = config["device_name"].asText()
        val apkPath = config["apk_path"].asText()
        val packageName = config["package_name"].asText()
        val mainActivity = config["main_activity"].asText()
        val targetResourceId = config["target_resource_id"].asText()

        try {
            // Start the Android emulator/device
            deviceManager.startDevice(deviceName)

            // Install the app on the emulator/device
            appManager.installApp(apkPath)

            // Run the test to measure app startup time and locate the target UI element
            val testPassed = AndroidTestCommands.measureAppStartupTime(packageName, mainActivity, targetResourceId)

            // Log the test result
            Logger.info(if (testPassed) "✅ Android Test Passed" else "❌ Android Test Failed")
        } catch (e: Exception) {
            // Log any errors during test execution
            Logger.error("❌ Error during Android test execution: ${e.message}")
        } finally {
            // Perform cleanup operations
            cleanup(deviceName, packageName)
        }
    }

    /**
     * Cleans up resources after the test execution.
     * Uninstalls the app and shuts down the emulator/device.
     *
     * @param deviceName The name of the Android emulator/device.
     * @param packageName The package name of the app to uninstall.
     */
    private fun cleanup(deviceName: String, packageName: String) {
        try {
            // Uninstall the app
            appManager.uninstallApp(packageName)
        } catch (e: Exception) {
            Logger.error("❌ Failed to uninstall Android app: ${e.message}")
        }
        try {
            // Shut down the emulator/device
            deviceManager.shutdownDevice(deviceName)
        } catch (e: Exception) {
            Logger.error("❌ Failed to shut down Android emulator: ${e.message}")
        }
    }
}