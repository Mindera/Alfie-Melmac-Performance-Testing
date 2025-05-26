package services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dtos.AvailableDeviceDTO
import java.io.File
import services.IServices.IDeviceService
import utils.Tools

class DeviceService : IDeviceService {

    override fun getAllAvailableDevices(): List<AvailableDeviceDTO> {
        return fetchAllDevices()
    }

    override fun getAvailableDevicesByMinVersion(minOsVersion: String): List<AvailableDeviceDTO> {
        val minVersionParts = minOsVersion.split(".").map { it.toIntOrNull() ?: 0 }

        return fetchAllDevices().filter { device ->
            compareVersions(device.osVersion, minVersionParts) >= 0
        }
    }

    override fun getDeviceBySerialNumber(serialNumber: String): AvailableDeviceDTO? {
        return fetchAllDevices().find { it.deviceSerialNumber == serialNumber }
    }

    override fun getDeviceByName(name: String): AvailableDeviceDTO? {
        return fetchAllDevices().find { it.deviceName == name }
    }

    private fun fetchAllDevices(): List<AvailableDeviceDTO> {
        val iosDevices = fetchIOSDevices()
        val androidDevices = fetchAndroidDevices()
        return iosDevices + androidDevices
    }

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

    private fun fetchIOSDevices(): List<AvailableDeviceDTO> {
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
