package ios

import core.AppManager
import utils.Logger

object IosAppManager : AppManager {
    override fun installApp(appPath: String) {
        Logger.info("Installing iOS app: $appPath")
        val process = ProcessBuilder("xcrun", "simctl", "install", "booted", appPath).start()
        process.waitFor()
        if (process.exitValue() != 0) {
            throw RuntimeException("Failed to install iOS app.")
        }
        Logger.info("✅ iOS app installed successfully.")
    }

    override fun uninstallApp(appId: String) {
        try {
            Logger.info("Uninstalling iOS app: $appId")
            ProcessBuilder("xcrun", "simctl", "uninstall", "booted", appId).start().waitFor()
            Logger.info("✅ iOS app uninstalled successfully.")
        } catch (e: Exception) {
            Logger.error("❌ Failed to uninstall iOS app: ${e.message}")
        }
    }
}