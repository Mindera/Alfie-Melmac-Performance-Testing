package android

import core.DeviceManager
import java.io.BufferedReader
import java.io.InputStreamReader
import utils.Logger

object AndroidDeviceManager : DeviceManager {
    override fun startDevice(deviceName: String) {
        Logger.info("📱 Starting Android emulator: $deviceName")
        val emulatorPath =
                "${System.getProperty("user.home")}/Library/Android/sdk/emulator/emulator"
        val process =
                ProcessBuilder(
                                emulatorPath,
                                "-avd",
                                deviceName,
                                "-no-snapshot-load",
                                "-no-snapshot-save",
                                "-no-boot-anim"
                        )
                        .start()

        Logger.info("⏳ Waiting for Android emulator device...")
        waitForDeviceBoot(process)
    }

    override fun shutdownDevice(deviceName: String) {
        try {
            Logger.info("Shutting down Android emulator: $deviceName")
            val emulatorSerial = getEmulatorSerial(deviceName)
            if (emulatorSerial != null) {
                Logger.info("Targeting emulator with serial: $emulatorSerial")
                val process = ProcessBuilder("adb", "-s", emulatorSerial, "emu", "kill").start()
                val exitCode = process.waitFor()
                if (exitCode == 0) {
                    Logger.info("✅ Android emulator shut down successfully!")
                } else {
                    val errorOutput = process.errorStream.bufferedReader().readText()
                    Logger.error("❌ Failed to shut down emulator. Error: $errorOutput")
                }
            } else {
                Logger.error("❌ Could not find emulator with name: $deviceName")
            }
        } catch (e: Exception) {
            Logger.error("❌ Failed to shut down Android emulator: ${e.message}")
        }
    }

    private fun getEmulatorSerial(deviceName: String): String? {
        try {
            Logger.info("Fetching emulator serial for device name: $deviceName")
            val process = ProcessBuilder("adb", "devices").start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val devices = reader.readLines()
            reader.close()
    
            for (line in devices) {
                if (line.startsWith("emulator-")) {
                    val serial = line.split("\\s+".toRegex())[0] // Extract the serial number
                    val avdNameProcess = ProcessBuilder("adb", "-s", serial, "shell", "getprop", "ro.boot.qemu.avd_name").start()
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

    private fun waitForDeviceBoot(process: Process) {
        val bootTimeout = 300 // Timeout in seconds
        var secondsWaited = 0

        ProcessBuilder("adb", "wait-for-device").start().waitFor()

        while (true) {
            val bootStatus =
                    ProcessBuilder("adb", "shell", "getprop", "sys.boot_completed")
                            .start()
                            .inputStream
                            .bufferedReader()
                            .readText()
                            .trim()

            if (bootStatus == "1") {
                Logger.info("✅ Android emulator boot completed!")
                break
            }

            if (secondsWaited >= bootTimeout) {
                Logger.error("❌ Timeout! Android emulator didn't boot in $bootTimeout seconds.")
                process.destroy()
                throw RuntimeException("Android emulator failed to boot within the timeout period.")
            }

            Logger.info("⏳ Still booting... waited ${secondsWaited}s")
            Thread.sleep(5000)
            secondsWaited += 5
        }
    }
}
