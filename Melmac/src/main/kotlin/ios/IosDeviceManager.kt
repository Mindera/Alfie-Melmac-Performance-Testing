package ios

import core.DeviceManager
import utils.Logger
import java.io.IOException

object IosDeviceManager : DeviceManager {
    override fun startDevice(deviceName: String) {
        Logger.info("Booting iOS Simulator: $deviceName")
        val deviceId = getSimulatorId(deviceName)
        val process = ProcessBuilder("xcrun", "simctl", "boot", deviceId).start()
        process.waitFor()
        if (process.exitValue() != 0) {
            throw IOException("Failed to boot iOS Simulator: $deviceName")
        }
        Logger.info("✅ iOS Simulator booted successfully!")
    
        // Explicitly open the Simulator app
        Logger.info("Opening iOS Simulator app...")
        val openProcess = ProcessBuilder("open", "-a", "Simulator").start()
        openProcess.waitFor()
        if (openProcess.exitValue() != 0) {
            throw IOException("Failed to open iOS Simulator app")
        }
        Logger.info("✅ iOS Simulator app opened successfully!")
    }

    override fun shutdownDevice(deviceName: String) {
        try {
            Logger.info("Shutting down iOS Simulator: $deviceName")
            val deviceId = getSimulatorId(deviceName)
            ProcessBuilder("xcrun", "simctl", "shutdown", deviceId).start().waitFor()
            Logger.info("✅ iOS Simulator shut down successfully!")
        } catch (e: Exception) {
            Logger.error("❌ Failed to shut down iOS Simulator: ${e.message}")
        }
    }

    private fun getSimulatorId(simulatorName: String): String {
        val process = ProcessBuilder("xcrun", "simctl", "list", "devices").start()
        process.waitFor()
        val output = process.inputStream.bufferedReader().readText()
        return Regex(".*($simulatorName).*([A-F0-9\\-]{36})").find(output)?.groupValues?.get(2)
            ?: throw Exception("Simulator not found: $simulatorName")
    }
}