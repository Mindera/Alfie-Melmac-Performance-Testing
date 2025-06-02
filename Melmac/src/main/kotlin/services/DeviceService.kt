package services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dtos.AvailableDeviceDTO
import java.io.File
import services.IServices.IDeviceService
import utils.Tools
import repos.IRepos.IDeviceRepository
import repos.IRepos.IOperSysVersionRepository
import repos.IRepos.IOperSysRepository

/**
 * Service implementation for managing device information.
 * Provides methods to retrieve device details from repositories and local system tools.
 *
 * @property deviceRepository Repository for device entities.
 * @property osVersionRepository Repository for OS version entities.
 * @property osRepository Repository for OS entities.
 */
class DeviceService (
        private val deviceRepository: IDeviceRepository,
        private val osVersionRepository: IOperSysVersionRepository,
        private val osRepository: IOperSysRepository
) : IDeviceService {

    /**
     * Retrieves a device by its ID.
     *
     * @param deviceId The ID of the device.
     * @return [AvailableDeviceDTO] for the device, or null if not found.
     */
    override fun getDeviceById(deviceId: Int): AvailableDeviceDTO? {
        val device = deviceRepository.findById(deviceId) ?: return null
        val osVersion = osVersionRepository.findById(device.osVersionOsVersionId)
                ?: return null
        val os = osRepository.findById(osVersion.operativeSystemOperSysId)
                ?: return null

        return AvailableDeviceDTO(
                id = device.deviceId,
                deviceName = device.deviceName,
                deviceSerialNumber = device.deviceSerialNumber,
                osName = os.operSysName,
                osVersion = osVersion.version
        )
    }

    /**
     * Retrieves all available devices (iOS and Android).
     *
     * @return List of [AvailableDeviceDTO] representing all available devices.
     */
    override fun getAllAvailableDevices(): List<AvailableDeviceDTO> {
        return fetchAllDevices()
    }

    /**
     * Retrieves available devices with a minimum OS version.
     *
     * @param minOsVersion The minimum OS version required.
     * @return List of [AvailableDeviceDTO] matching the criteria.
     */
    override fun getAvailableDevicesByMinVersion(minOsVersion: String): List<AvailableDeviceDTO> {
        val minVersionParts = minOsVersion.split(".").map { it.toIntOrNull() ?: 0 }

        return fetchAllDevices().filter { device ->
            compareVersions(device.osVersion, minVersionParts) >= 0
        }
    }

    /**
     * Retrieves a device by its serial number.
     *
     * @param serialNumber The serial number of the device.
     * @return [AvailableDeviceDTO] for the device, or null if not found.
     */
    override fun getDeviceBySerialNumber(serialNumber: String): AvailableDeviceDTO? {
        return fetchAllDevices().find { it.deviceSerialNumber == serialNumber }
    }

    /**
     * Retrieves a device by its name.
     *
     * @param name The name of the device.
     * @return [AvailableDeviceDTO] for the device, or null if not found.
     */
    override fun getDeviceByName(name: String): AvailableDeviceDTO? {
        return fetchAllDevices().find { it.deviceName == name }
    }

    /**
     * Fetches all available devices (iOS and Android).
     *
     * @return List of [AvailableDeviceDTO] for all devices.
     */
    private fun fetchAllDevices(): List<AvailableDeviceDTO> {
        val iosDevices = fetchIOSDevices()
        val androidDevices = fetchAndroidDevices()
        return iosDevices + androidDevices
    }

    /**
     * Compares two version strings.
     *
     * @param versionStr The version string to compare.
     * @param minVersionParts The minimum version as a list of integers.
     * @return Positive if versionStr >= minVersionParts, negative otherwise.
     */
    private fun compareVersions(versionStr: String, minVersionParts: List<Int>): Int {
        val versionParts = versionStr.split(".").map { it.toIntOrNull() ?: 0 }

        for (i in 0 until maxOf(versionParts.size, minVersionParts.size)) {
            val vPart = versionParts.getOrElse(i) { 0 }
            val minPart = minVersionParts.getOrElse(i) { 0 }

            if (vPart != minPart) {
                return vPart - minPart
            }
        }
        return 0
    }

    /**
     * Fetches all available iOS devices using simctl (only on macOS).
     *
     * @return List of [AvailableDeviceDTO] for iOS devices.
     */
    private fun fetchIOSDevices(): List<AvailableDeviceDTO> {
        if (!Tools.isMac()) {
            return emptyList()
        }
        val output = Tools.run("xcrun simctl list devices -j")
        val mapper = jacksonObjectMapper()
        val root = mapper.readTree(output)
        val devicesNode = root["devices"] ?: return emptyList()

        val result = mutableListOf<AvailableDeviceDTO>()

        for ((runtimeKey, deviceArray) in devicesNode.fields()) {
            if (!runtimeKey.startsWith("com.apple.CoreSimulator.SimRuntime.iOS")) continue

            val version = runtimeKey.substringAfterLast(".iOS-").replace("-", ".")

            for (device in deviceArray) {
                val name = device["name"]?.asText() ?: continue
                val available = device["isAvailable"]?.asBoolean() ?: false
                val udid = device["udid"]?.asText() ?: name

                if (available) {
                    result.add(
                            AvailableDeviceDTO(
                                    id = null,
                                    deviceName = name,
                                    deviceSerialNumber = udid,
                                    osName = "iOS",
                                    osVersion = version
                            )
                    )
                }
            }
        }

        return result
    }

    /**
     * Fetches all available Android Virtual Devices (AVDs).
     *
     * @return List of [AvailableDeviceDTO] for Android devices.
     */
    private fun fetchAndroidDevices(): List<AvailableDeviceDTO> {
        val avds = readAVDsFromDirectory()

        return avds.map { avdName ->
            val version = readAndroidAVDVersion(avdName)
            AvailableDeviceDTO(
                    id = null,
                    deviceName = avdName,
                    deviceSerialNumber = null,
                    osName = "Android",
                    osVersion = version
            )
        }
    }

    /**
     * Reads all Android Virtual Device (AVD) names from the user's .android/avd directory.
     *
     * @return List of AVD names.
     */
    private fun readAVDsFromDirectory(): List<String> {
        val avdDir = File(System.getProperty("user.home"), ".android/avd")

        return avdDir.listFiles { file -> file.isFile && file.name.endsWith(".ini") }?.mapNotNull {
                iniFile ->
            val avdId =
                    iniFile.readLines()
                            .firstOrNull { it.startsWith("AvdId=") }
                            ?.substringAfter("AvdId=")
                            ?.trim()
                            ?: iniFile.name.removeSuffix(".ini")
            avdId
        }
                ?: emptyList()
    }

    /**
     * Reads the Android OS version for a given AVD name.
     *
     * @param avdName The name of the AVD.
     * @return The OS version as a string, or "unknown" if not found.
     */
    private fun readAndroidAVDVersion(avdName: String): String {
        val iniFile = File(System.getProperty("user.home"), ".android/avd/$avdName.ini")
        if (!iniFile.exists()) return "unknown"

        val lines = iniFile.readLines()

        lines.firstOrNull { it.startsWith("target=android-") }?.let {
            return it.substringAfter("target=android-").trim()
        }

        return "unknown"
    }
}