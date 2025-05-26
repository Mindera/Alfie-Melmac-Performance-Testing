package ios

import core.AppManager
import utils.*
import config.Config

/**
 * Manages iOS app installation and uninstallation on a booted iOS simulator.
 *
 * This object provides functionality to install and uninstall iOS apps
 * using the `xcrun simctl` command-line tool.
 */
object IOSAppManager : AppManager {

    /**
     * Installs an iOS app on the currently booted simulator.
     *
     * @param appPath The file path to the `.app` bundle to be installed.
     * @throws RuntimeException If the installation process fails.
     */
    override fun installApp(app: String) {
        val resolvedAppPath = Tools.resolvePath(Config.getAppFolderPath()) + '/' + app
        Logger.info("Installing iOS app: $app")
        val process = ProcessBuilder("xcrun", "simctl", "install", "booted", resolvedAppPath)
            .redirectErrorStream(true)
            .start()
        process.waitFor()
        if (process.exitValue() != 0) {
            throw RuntimeException("Failed to install iOS app.")
        }
        Logger.info("✅ iOS app installed successfully.")
    }

    /**
     * Uninstalls an iOS app from the currently booted simulator.
     *
     * @param appId The bundle identifier of the app to be uninstalled.
     */
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