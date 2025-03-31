package android

import core.AppManager
import utils.Logger

object AndroidAppManager : AppManager {
    override fun installApp(appPath: String) {
        Logger.info("Installing Android app: $appPath")
        val process = ProcessBuilder("adb", "install", "-r", appPath).start()
        process.waitFor()
        if (process.exitValue() != 0) {
            throw RuntimeException("Failed to install Android app.")
        }
        Logger.info("✅ Android app installed successfully.")
    }

    override fun uninstallApp(appId: String) {
        try {
            Logger.info("Uninstalling Android app: $appId")
            ProcessBuilder("adb", "uninstall", appId).start().waitFor()
            Logger.info("✅ Android app uninstalled successfully.")
        } catch (e: Exception) {
            Logger.error("❌ Failed to uninstall Android app: ${e.message}")
        }
    }
}