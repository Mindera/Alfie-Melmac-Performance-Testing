package android

import com.fasterxml.jackson.databind.JsonNode
import core.AppManager
import core.DeviceManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import utils.Logger

class AndroidTestRunner : KoinComponent {
    private val deviceManager: DeviceManager by inject(named("android"))
    private val appManager: AppManager by inject(named("android"))

    fun run(config: JsonNode) {
        val deviceName = config["device_name"].asText()
        val apkPath = config["apk_path"].asText()
        val packageName = config["package_name"].asText()
        val mainActivity = config["main_activity"].asText()
        val targetResourceId = config["target_resource_id"].asText()

        try {
            // Start the Android emulator
            deviceManager.startDevice(deviceName)

            // Install the app
            appManager.installApp(apkPath)

            // Run the test
            val testPassed = AndroidTestCommands.measureAppStartupTime(packageName, mainActivity, targetResourceId)

            Logger.info(if (testPassed) "✅ Android Test Passed" else "❌ Android Test Failed")
        } catch (e: Exception) {
            Logger.error("❌ Error during Android test execution: ${e.message}")
        } finally {
            // Cleanup operations
            try {
                appManager.uninstallApp(packageName)
            } catch (e: Exception) {
                Logger.error("❌ Failed to uninstall Android app: ${e.message}")
            }
            try {
                deviceManager.shutdownDevice(deviceName)
            } catch (e: Exception) {
                Logger.error("❌ Failed to shut down Android emulator: ${e.message}")
            }
        }
    }
}