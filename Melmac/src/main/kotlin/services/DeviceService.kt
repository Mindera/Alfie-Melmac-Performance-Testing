package services

import domain.Device
import domain.OS
import domain.OSVersion
import domain.dtos.DeviceResponseDTO
import domain.dtos.OSResponseDTO
import domain.dtos.OSVersionResponseDTO
import repos.IRepos.IDeviceRepository
import repos.IRepos.IOSRepository
import repos.IRepos.IOSVersionRepository
import services.IServices.IDeviceService
import utils.Tools

class DeviceService(
        private val osRepo: IOSRepository,
        private val osVersionRepo: IOSVersionRepository,
        private val deviceRepo: IDeviceRepository
) : IDeviceService {

    override fun syncAllDevices() {
        syncIOSDevices()
        syncAndroidDevices()
    }

    override fun getAllDevices(): List<DeviceResponseDTO> =
            deviceRepo.findAll().map {
                DeviceResponseDTO(id = it.id, name = it.name, osVersionId = it.osVersionId)
            }

    override fun getAllOS(): List<OSResponseDTO> =
            osRepo.findAll().map { OSResponseDTO(id = it.id!!, name = it.name) }

    override fun getAllOSVersions(): List<OSVersionResponseDTO> =
            osVersionRepo.findAll().map {
                OSVersionResponseDTO(id = it.id!!, versionName = it.versionName, osId = it.osId)
            }

    private fun syncIOSDevices() {
        val os = getOrCreateOS("iOS")
        val output = Tools.run("xcrun simctl list devices -j")

        val devices = parseIOSDevices(output)
        devices.forEach { device ->
            val osVersion = getOrCreateOSVersion(device.second, os.id!!)
            val d = Device(id = device.first, name = device.first, osVersionId = osVersion.id!!)
            if (deviceRepo.findById(d.id) == null) {
                deviceRepo.save(d)
            }
        }
    }

    private fun syncAndroidDevices() {
        val os = getOrCreateOS("Android")

        val output = Tools.run("emulator -list-avds")
        val avds =
                if (output.startsWith("ERROR:")) {
                    readAVDsFromDirectory()
                } else {
                    output.lines().filter { it.isNotBlank() }.map { it.trim() }
                }

        avds.forEach { avdName ->
            val versionName = readAndroidAVDVersion(avdName)
            val osVersion = getOrCreateOSVersion(versionName, os.id!!)
            val d = Device(id = avdName, name = avdName, osVersionId = osVersion.id!!)
            if (deviceRepo.findById(d.id) == null) {
                deviceRepo.save(d)
            }
        }
    }

    private fun getOrCreateOS(name: String): OS {
        return osRepo.findByName(name)
                ?: run {
                    val id = osRepo.save(OS(name = name))
                    OS(id = id, name = name)
                }
    }

    private fun getOrCreateOSVersion(version: String, osId: Int): OSVersion {
        return osVersionRepo.findByVersionNameAndOS(version, osId)
                ?: run {
                    val id = osVersionRepo.save(OSVersion(versionName = version, osId = osId))
                    OSVersion(id = id, versionName = version, osId = osId)
                }
    }

    private fun parseIOSDevices(json: String): List<Pair<String, String>> {
        val mapper = com.fasterxml.jackson.module.kotlin.jacksonObjectMapper()
        val root = mapper.readTree(json)
        val devicesNode = root["devices"] ?: return emptyList()

        val result = mutableListOf<Pair<String, String>>()

        for ((runtimeKey, deviceArray) in devicesNode.fields()) {
            if (!runtimeKey.startsWith("com.apple.CoreSimulator.SimRuntime.iOS")) continue

            val version = runtimeKey.substringAfterLast(".iOS-").replace("-", ".")

            for (device in deviceArray) {
                val name = device["name"]?.asText() ?: continue
                val available = device["isAvailable"]?.asBoolean() ?: false
                val udid = device["udid"]?.asText() ?: name

                if (available) {
                    result.add(udid to version)
                }
            }
        }

        return result
    }

    private fun readAndroidAVDVersion(avdName: String): String {
        val home = System.getProperty("user.home")
        val configPath = "$home/.android/avd/$avdName.avd/config.ini"
        val file = java.io.File(configPath)

        if (!file.exists()) return "unknown"

        val configLines = file.readLines()

        configLines.find { it.startsWith("target=android-") }?.let {
            return it.substringAfter("android-").trim()
        }

        configLines.find { it.contains("android-") }?.let {
            return it.substringAfter("android-").substringBefore("/").trim()
        }

        return "unknown"
    }

    private fun readAVDsFromDirectory(): List<String> {
        val home = System.getProperty("user.home")
        val avdPath = java.io.File("$home/.android/avd")
        return avdPath.listFiles { file -> file.isDirectory && file.name.endsWith(".avd") }?.map {
            it.name.removeSuffix(".avd")
        }
                ?: emptyList()
    }
}
