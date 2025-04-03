package core

/**
 * Interface for managing devices (emulators or physical devices).
 * Provides methods to start and shut down devices.
 */
interface DeviceManager {

    /**
     * Starts the specified device (emulator or physical device).
     *
     * @param deviceName The name of the device to start (e.g., AVD name for Android or simulator name for iOS).
     */
    fun startDevice(deviceName: String)

    /**
     * Shuts down the specified device (emulator or physical device).
     *
     * @param deviceName The name of the device to shut down (e.g., AVD name for Android or simulator name for iOS).
     */
    fun shutdownDevice(deviceName: String)
}