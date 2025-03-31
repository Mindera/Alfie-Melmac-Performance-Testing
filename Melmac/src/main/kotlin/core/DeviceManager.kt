package core

interface DeviceManager {
    fun startDevice(deviceName: String)
    fun shutdownDevice(deviceName: String)
}