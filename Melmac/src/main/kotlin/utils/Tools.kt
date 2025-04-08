package utils

import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Utility object for tools 
 * Provides methods to perform various utility functions.
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
            .find(output)?.groupValues?.get(1)
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
                    val avdNameProcess = ProcessBuilder(
                        "adb", "-s", serial, "shell", "getprop", "ro.boot.qemu.avd_name"
                    ).start()
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
}