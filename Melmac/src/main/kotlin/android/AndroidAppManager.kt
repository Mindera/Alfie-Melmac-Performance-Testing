package android

import core.AppManager
import utils.Logger

/**
 * Object responsible for managing Android apps.
 * Provides functionality to install and uninstall Android apps using ADB commands.
 */
object AndroidAppManager : AppManager {

    /**
     * Installs an Android app on the connected device or emulator.
     *
     * @param appPath The file path to the APK to be installed.
     * @throws RuntimeException if the installation fails.
     */
    override fun installApp(appPath: String) {
        Logger.info("Installing Android app: $appPath")
        val process = ProcessBuilder("adb", "install", "-r", appPath).start()
        process.waitFor()
        if (process.exitValue() != 0) {
            throw RuntimeException("Failed to install Android app.")
        }
        Logger.info("✅ Android app installed successfully.")
    }

    /**
     * Uninstalls an Android app from the connected device or emulator.
     *
     * @param appId The package name of the app to be uninstalled.
     */
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