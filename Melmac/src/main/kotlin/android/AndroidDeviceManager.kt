package android

import core.DeviceManager
import utils.Logger
import utils.Tools

/**
 * Object responsible for managing Android devices (emulators).
 * Provides functionality to start and shut down Android emulators.
 */
object AndroidDeviceManager : DeviceManager {

    /**
     * Starts the specified Android emulator.
     *
     * @param deviceName The name of the Android emulator (AVD) to start.
     * @throws RuntimeException if the emulator fails to boot within the timeout period.
     */
    override fun startDevice(deviceName: String) {
        Logger.info("📱 Starting Android emulator: $deviceName")
        val sdkHome =
            System.getenv("ANDROID_HOME")
                ?: System.getenv("ANDROID_SDK_ROOT")
                ?: "${System.getProperty("user.home")}/Library/Android/sdk"
        val emulatorPath = "$sdkHome/emulator/emulator"

        val args =
            mutableListOf(
                emulatorPath,
                "-avd",
                deviceName,
                "-no-snapshot-load",
                "-no-snapshot-save",
                "-no-boot-anim",
                "-no-window",
                "-no-audio",
            )

        val process = ProcessBuilder(args).redirectErrorStream(true).start()

        Thread {
            process.inputStream.bufferedReader().useLines { lines ->
                lines.forEach { Logger.info("[emulator] $it") }
            }
        }.start()

        Logger.info("⏳ Waiting for Android emulator device...")
        waitForDeviceBoot(process)
    }

    /**
     * Shuts down the specified Android emulator.
     *
     * @param deviceName The name of the Android emulator (AVD) to shut down.
     */
    override fun shutdownDevice(deviceName: String) {
        try {
            Logger.info("Shutting down Android emulator: $deviceName")
            val emulatorSerial = Tools.getEmulatorSerial(deviceName)
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

    /**
     * Waits for the Android emulator to complete the boot process.
     *
     * @param process The process of the emulator being started.
     * @throws RuntimeException if the emulator fails to boot within the timeout period or if the package manager is not ready.
     */
    private fun waitForDeviceBoot(process: Process) {
        val bootTimeout = 900
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
        Logger.info("⏳ Waiting for Android package manager to be fully functional...")
        var pmReady = false
        var pmTries = 0
        while (!pmReady && pmTries < 60) {
            val pmOutput =
                ProcessBuilder("adb", "shell", "pm", "path", "android")
                    .redirectErrorStream(true)
                    .start()
                    .inputStream
                    .bufferedReader()
                    .readText()
                    .trim()

            if (pmOutput.startsWith("package:")) {
                pmReady = true
                Logger.info("✅ Android package manager is fully functional.")
            } else {
                Logger.info("⏳ Still waiting for package manager... attempt ${pmTries + 1}")
                Thread.sleep(2000)
                pmTries++
            }
        }
        if (!pmReady) {
            Logger.error("❌ Timeout! Android package manager not ready after waiting.")
            process.destroy()
            throw RuntimeException("Android package manager not ready after waiting.")
        }
    }
}