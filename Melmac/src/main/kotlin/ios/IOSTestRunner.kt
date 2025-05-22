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
     * - `target_resource_id`: The accessibility ID of the UI element to wait for.
     * - `bundle_id`: The bundle identifier of the app to uninstall after the test.
     * - `driver_runner_path`: Path to the Xcode project folder (e.g., `../../DriverRunner`)
     */
    fun run(config: JsonNode) {
        val deviceName = config["device_name"].asText()
        val appPath = config["app_path"].asText()
        val targetResourceId = config["target_resource_id"].asText()
        val bundleId = config["bundle_id"].asText()

        try {
            // Start the iOS simulator
            Logger.info("🚀 Starting iOS test on device: $deviceName")
            deviceManager.startDevice(deviceName)

            // Install the app
            Logger.info("📲 Installing app at $appPath")
            appManager.installApp(appPath)

            // Run XCUITest via HTTP runner
            Logger.info("🧪 Running test for element: $targetResourceId")
            val testPassed = XCUITestCommands.runLaunchTestForElement(
                bundleId = bundleId,
                elementId = targetResourceId,
                deviceName = deviceName
            )

            Logger.info(if (testPassed) "✅ iOS Test Passed" else "❌ iOS Test Failed")
        } catch (e: Exception) {
            Logger.error("❌ Error during iOS test execution: ${e.message}")
        } finally {
            // Cleanup operations
            try {
                Logger.info("🧼 Uninstalling app: $bundleId")
            } catch (e: Exception) {
                Logger.error("❌ Failed to uninstall iOS app: ${e.message}")
            }
            try {
                Logger.info("🧯 Shutting down simulator: $deviceName")
                deviceManager.shutdownDevice(deviceName)
            } catch (e: Exception) {
                Logger.error("❌ Failed to shut down iOS simulator: ${e.message}")
            }
        }
    }
}