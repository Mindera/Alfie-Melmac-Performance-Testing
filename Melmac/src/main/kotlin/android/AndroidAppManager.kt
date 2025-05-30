package android

import core.AppManager
import java.nio.file.Paths
import utils.*

/**
 * Object responsible for managing Android apps. Provides functionality to install and uninstall
 * Android apps using ADB commands.
 */
object AndroidAppManager : AppManager {

    /**
     * Installs an Android app on the connected device or emulator.
     *
     * @param appPath The file path to the APK to be installed.
     * @throws RuntimeException if the installation fails.
     */
    override fun installApp(app: String) {
        val appPath =
                Paths.get(System.getProperty("user.dir"), "src/main/resources/apps").resolve(app)
        Logger.info("Resolved APK path: $appPath")
        if (!appPath.toFile().exists()) {
            throw RuntimeException("APK not found at $appPath")
        }
        Logger.info("Installing Android app: $app")

        var success = false
        var attempts = 0
        var output = ""
        while (!success && attempts < 3) {
            val process =
                    ProcessBuilder("adb", "install", "-r", appPath.toString())
                            .redirectErrorStream(true)
                            .start()
            output = process.inputStream.bufferedReader().readText()
            process.waitFor()
            Logger.info("adb install output (attempt ${attempts + 1}):\n$output")
            if (process.exitValue() == 0 && !output.contains("Failure")) {
                success = true
            } else {
                Logger.error("Install failed, retrying in 5 seconds...")
                Thread.sleep(5000)
            }
            attempts++
        }
        if (!success) {
            throw RuntimeException(
                    "Failed to install Android app after $attempts attempts. adb output:\n$output"
            )
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
