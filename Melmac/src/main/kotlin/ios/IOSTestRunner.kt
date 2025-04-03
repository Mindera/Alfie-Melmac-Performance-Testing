package ios

import com.fasterxml.jackson.databind.JsonNode
import core.AppManager
import core.DeviceManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import utils.Logger

/**
 * Class responsible for running iOS tests.
 * Manages the lifecycle of the iOS simulator, app installation, test execution, and cleanup.
 */
class IOSTestRunner : KoinComponent {

    // Dependency injection for managing iOS devices
    private val deviceManager: DeviceManager by inject(named("ios"))

    // Dependency injection for managing iOS apps
    private val appManager: AppManager by inject(named("ios"))

    /**
     * Executes the iOS test based on the provided configuration.
     *
     * @param config A JSON node containing the test configuration.
     * - `device_name`: The name of the iOS simulator to use.
     * - `app_path`: The file path to the `.app` bundle to be installed.
     * - `target_resource_id`: The resource ID of the target UI element to locate.
     * - `bundle_id`: The bundle identifier of the app to be uninstalled after the test.
     */
    fun run(config: JsonNode) {
        val deviceName = config["device_name"].asText()
        val appPath = config["app_path"].asText()
        val targetResourceId = config["target_resource_id"].asText()
        val appId = config["bundle_id"].asText()

        try {
            // Start the iOS simulator
            Logger.info("🚀 Starting iOS test...")
            deviceManager.startDevice(deviceName)

            // Install the app
            appManager.installApp(appPath)

            // Run XCUITest
            val testPassed = XCUITestCommands.runXCUITest(targetResourceId)

            Logger.info(if (testPassed) "✅ iOS Test Passed" else "❌ iOS Test Failed")
        } catch (e: Exception) {
            Logger.error("❌ Error during iOS test execution: ${e.message}")
        } finally {
            // Cleanup operations
            try {
                appManager.uninstallApp(appId) // Uninstall the app using `bundle_id`
            } catch (e: Exception) {
                Logger.error("❌ Failed to uninstall iOS app: ${e.message}")
            }
            try {
                deviceManager.shutdownDevice(deviceName) // Shut down the simulator
            } catch (e: Exception) {
                Logger.error("❌ Failed to shut down iOS simulator: ${e.message}")
            }
        }
    }
}