package utils

import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Locale

/**
 * Utility object for tools.
 * Provides methods to perform various utility functions such as device ID retrieval,
 * command execution, path resolution, and version extraction for APK and iOS app bundles.
 */
object Tools {

    /**
     * Retrieves the unique device ID of the specified iOS simulator.
     *
     * @param simulatorName The name of the iOS simulator.
     * @return The unique device ID of the simulator.
     * @throws Exception if the simulator is not found.
     */
    fun getSimulatorIdforIOS(simulatorName: String): String {
        val process = ProcessBuilder("xcrun", "simctl", "list", "devices").start()
        process.waitFor()
        val output = process.inputStream.bufferedReader().readText()
        return Regex(".*$simulatorName \\(([-A-F0-9]+)\\) \\(.*\\)")
                .find(output)
                ?.groupValues
                ?.get(1)
                ?: throw Exception("Simulator not found: $simulatorName")
    }

    /**
     * Retrieves the serial number of the specified Android emulator.
     *
     * @param deviceName The name of the Android emulator (AVD).
     * @return The serial number of the emulator, or null if not found.
     */
    fun getEmulatorSerial(deviceName: String): String? {
        try {
            Logger.info("Fetching emulator serial for device name: $deviceName")
            val process = ProcessBuilder("adb", "devices").start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val devices = reader.readLines()
            reader.close()

            for (line in devices) {
                if (line.startsWith("emulator-")) {
                    val serial = line.split("\\s+".toRegex())[0] // Extract the serial number
                    val avdNameProcess =
                            ProcessBuilder(
                                            "adb",
                                            "-s",
                                            serial,
                                            "shell",
                                            "getprop",
                                            "ro.boot.qemu.avd_name"
                                    )
                                    .start()
                    val avdName = avdNameProcess.inputStream.bufferedReader().readText().trim()
                    if (avdName == deviceName) {
                        Logger.info("Match found! Serial: $serial for AVD name: $avdName")
                        return serial
                    }
                }
            }
        } catch (e: Exception) {
            Logger.error("❌ Error while fetching emulator serial: ${e.message}")
        }
        Logger.error("❌ No matching emulator found for device name: $deviceName")
        return null
    }

    /**
     * Runs a shell command and returns its output as a string.
     *
     * @param command The command to execute.
     * @return The output of the command, or an error message if execution fails.
     */
    fun run(command: String): String {
        return try {
            val process =
                    ProcessBuilder(*command.split(" ").toTypedArray())
                            .redirectErrorStream(true)
                            .start()

            val output = BufferedReader(InputStreamReader(process.inputStream)).readText()
            process.waitFor()
            output
        } catch (e: Exception) {
            "ERROR: ${e.message}"
        }
    }

    /**
     * Resolves a path to an absolute path, supporting both absolute and relative input.
     * If the input is already absolute, returns it as-is. If relative, resolves it from the current
     * working directory.
     *
     * @param path The path to resolve.
     * @return The absolute path as a string.
     */
    fun resolvePath(path: String): String {
        val expanded =
                if (path.startsWith("~")) {
                    System.getProperty("user.home") + path.drop(1)
                } else path
        val file = java.io.File(expanded)
        return if (file.isAbsolute) file.absolutePath
        else java.io.File(System.getProperty("user.dir"), expanded).absolutePath
    }

    /**
     * Retrieves the version of an APK file.
     *
     * @param apkPath The file path to the APK.
     * @return The version name of the APK, or null if not found.
     */
    fun getApkVersion(apkPath: String): String? {
        val aaptPath = Tools.resolvePath("~/Library/Android/sdk/build-tools/34.0.0/aapt")
        val process = ProcessBuilder(aaptPath, "dump", "badging", apkPath).start()
        val output = process.inputStream.bufferedReader().readText()
        val match = Regex("versionName='([^']+)'").find(output)
        return match?.groups?.get(1)?.value
    }

    /**
     * Retrieves the version of an iOS app bundle.
     *
     * @param appPath The file path to the app bundle.
     * @return The version of the app bundle, or null if not found.
     */
    fun getAppBundleVersion(appPath: String): String? {
        val plistPath = "$appPath/Info.plist"
        val process =
                ProcessBuilder(
                                "/usr/libexec/PlistBuddy",
                                "-c",
                                "Print :CFBundleShortVersionString",
                                plistPath
                        )
                        .start()
        return process.inputStream.bufferedReader().readText().trim().ifEmpty { null }
    }

    /**
     * Checks if the current operating system is macOS.
     *
     * @return true if the OS is macOS, false otherwise.
     */
    fun isMac(): Boolean {
        return System.getProperty("os.name").lowercase(Locale.getDefault()).contains("mac")
    }
}